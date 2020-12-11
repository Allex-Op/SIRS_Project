package sirs.api.hospital;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sirs.api.hospital.accessControl.ResourceId;
import sirs.api.hospital.db.Repo;
import sirs.api.hospital.entities.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import sirs.api.hospital.messages.*;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.file.Files;


@RestController
public class Handlers {
    Repo repo = new Repo();
    Crypto cr = new Crypto();
    CustomProtocol customProtocol = new CustomProtocol();

    String LAB_URL = System.getenv("LAB_URL");
    String PROJECT_PATH = System.getenv("PROJECT_PATH");

    @GetMapping("/secret")
    @ResourceId(resourceId = "secret")
    public ResponseEntity<String> secret() {
        return ResponseEntity.ok("top secret endpoint for debugging");
    }

    @GetMapping("/patient/{id}/name")
    @ResourceId(resourceId = "getPatientName")
    public ResponseEntity<Patient> getPatientName(@PathVariable int id) {
        Patient name = repo.getPatientName(id);
        if(name == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(name);
    }

    @GetMapping("/patient/{id}/diseases")
    @ResourceId(resourceId = "getPatientDiseases")
    public ResponseEntity<Patient> getPatientDiseases(@PathVariable int id) {
        Patient diseases = repo.getPatientDiseases(id);
        if(diseases == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(diseases);
    }

    @GetMapping("/patient/{id}/testresults")
    @ResourceId(resourceId = "getPatientTestResults")
    public ResponseEntity<Patient> getPatientTestResults(@PathVariable int id) {
        Patient results = repo.getPatientTestResults(id);
        if(results == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/patient/{id}/treatment")
    @ResourceId(resourceId = "getPatientTreatment")
    public ResponseEntity<Patient> getPatientTreatment(@PathVariable int id) {
        Patient treatment = repo.getPatientTreatment(id);
        if(treatment == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(treatment);
    }

    @PostMapping("/login")
    @ResourceId(resourceId = "login")
    public ResponseEntity<TokenEntity> login(@RequestBody LoginBody credentials) {
        // Create new session or return already existing token for session
        String token = repo.getSessionToken(credentials);

        if(!token.equals("")) {
            System.out.println("[Handlers - Login] Authentication succeeded.");
            return ResponseEntity.ok(new TokenEntity(token));
        } else {
            System.out.println("[Handlers - Login] Authentication failed.");
            return ResponseEntity.status(401).build();  // In case the authentication failed
        }
    }

    @GetMapping("/checkauthenticity/{id}")
    @ResourceId(resourceId = "checkAuthenticity")
    public ResponseEntity<AuthenticityCheck> getTestAuthenticity(@PathVariable int id) {
        String[] info = repo.getResult(id);
        if(info[0] == null || info[1] == null || info[2] == null)
            return ResponseEntity.notFound().build();

        if(cr.verifySignature(info[0], info[1], info[2]))
            return ResponseEntity.ok(new AuthenticityCheck("Test result is authentic!"));
        else
            return ResponseEntity.ok(new AuthenticityCheck("Test results validity is compromised, do not use it."));
    }

    /**
     *  Requests test results from the lab, this handler must create & handle the custom secure channel.
    */
    @JsonIgnoreProperties
    @GetMapping("/gettestresults/{id}")
    @ResourceId(resourceId = "getTestsResult")
    public ResponseEntity<String> sendTestToLab(@PathVariable int id) {
        try {
            System.out.println("[Debug] Starting test results to lab request...");
            CustomProtocolResponse cp2Response = initHandshake();
            if(cp2Response == null)
                return ResponseEntity.status(500).build();

            HandshakeResponse hsResponse = cp2Response.getHandshakeResponse();
            System.out.println("[Debug] Received handshake response from lab...");

            //Generate secret key
            String labPubKey = hsResponse.getLabKeyString();
            customProtocol.generateSharedSecret(labPubKey);

            // Evaluates the integrity of the response and validates its not replayed
            if(customProtocol.dataCheck(cp2Response.getMac()) && customProtocol.verifyNonce(hsResponse.getNonce())) {
                System.out.println("[Debug] Requesting test results from lab...");
                CustomProtocolResponse cpResponse = requestTestResults(id);
                if(cpResponse == null)
                    return ResponseEntity.status(500).build();
                System.out.println("[Debug] Received test results from lab...");

                if(customProtocol.dataCheck(cpResponse.getMac())) {
                    TestResponse testResponse = cpResponse.getTestResponse();
                    if(customProtocol.verifyNonce(testResponse.getNonce())) {

                        // decrypting the results
                        String decryptedResults = customProtocol.decryptWithSecretKey(testResponse.getResults(), cpResponse.getIv());
                        System.out.println(decryptedResults);
                        if(!repo.insertTestResultsFromLab(2, decryptedResults, 1, testResponse.getDigitalSignature())) {
                            System.out.println("Error saving the test results received from the Lab");
                            return ResponseEntity.status(500).build();
                        }

                        return ResponseEntity.ok(decryptedResults);
                    }
                }
            }

            System.out.println("[Debug] An attack is happening? Freshness or Integrity test failed.");
            return ResponseEntity.status(500).build();
        } catch(Exception e) {
            System.out.println("GetTestResults failed");
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     *  Initiates an handshake with the lab with the goal
     *  to generate the session keys used for the request & transmission
     *  of the test results.
     *
     *  Returns a 'CustomProtocolResponse' object which contains in an encrypted way
     *  the results, nonce and digitalSignature.
     */
    private CustomProtocolResponse initHandshake() {
        try {
            System.out.println("[Debug] Preparing init handshake request...");

            // Getting the certificate
            File crtFile = new File(PROJECT_PATH + "certificates/hospital.pem");
            String certificate = Files.readString(crtFile.toPath(), Charset.defaultCharset());
            String hospitalPubKey = customProtocol.diffieHospitalPublicKey();
            HandshakeRequest handshakeRequest = new HandshakeRequest(certificate, hospitalPubKey);

            // Write body
            ObjectMapper mapper = new ObjectMapper();

            // Send certificate and receive response that makes possible to create the secret key
            String reqBody = mapper.writeValueAsString(handshakeRequest);
            HttpClient handshakeClient = HttpClient.newHttpClient();
            HttpRequest handshakeReq = HttpRequest.newBuilder()
                    .uri(URI.create(LAB_URL + "/beginhandshake"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();

            HttpResponse<String> handshakeResponse = handshakeClient.send(handshakeReq, HttpResponse.BodyHandlers.ofString());
            String body = handshakeResponse.body();
            return mapper.readValue(body, CustomProtocolResponse.class);

        } catch(Exception e) {
            System.out.println("Error during handshake with Lab");
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Called after the handshake.
     * Creates a test results request with id X and receives
     * the tests results associated with that id.
     *
     * Data is protected using session keys generated during handshake.
     *
     * Returns a 'CustomProtocolResponse'
     */
    private CustomProtocolResponse requestTestResults(int id) {
        try {
            System.out.println("[Debug] Preparing test results request...");
            ObjectMapper mapper = new ObjectMapper();

            String[] testReqValues = customProtocol.encryptWithSecretKey(String.valueOf(id));
            TestRequest testRequest = new TestRequest(testReqValues[0], customProtocol.createNonce());

            // Using mapper to transform testResponse into string
            // Doing mac of the resulting string, generating the data string meant to put in customProtocolResponse
            String req = mapper.writeValueAsString(testRequest);
            String mac = customProtocol.macMessage(req.getBytes());

            // mac = tag + respData (json string -> handshakeResponse)
            ProtectedTestRequest protectedTestRequest = new ProtectedTestRequest(mac, testReqValues[1]);

            // Sending the testReq (including data and nonce)
            String testReqBody = mapper.writeValueAsString(protectedTestRequest);

            System.out.println("[Debug] Sending Protected test request to lab...");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LAB_URL + "/teststoanalyze"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(testReqBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("[Debug] Received test request response from lab...");
            //Read Response
            String body = response.body();
            return mapper.readValue(body, CustomProtocolResponse.class);
        } catch(Exception e) {
            System.out.println("Error during test results request");
            System.out.println(e.getMessage());
            return null;
        }
    }
}

