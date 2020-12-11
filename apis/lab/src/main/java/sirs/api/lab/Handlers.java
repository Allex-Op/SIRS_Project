package sirs.api.lab;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sirs.api.lab.entities.*;

import javax.crypto.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;

@RestController
public class Handlers {
    CustomProtocol customProtocol = new CustomProtocol();
    String PROJECT_PATH = System.getenv("PROJECT_PATH");

    @PostMapping("/beginhandshake")
    public ResponseEntity<CustomProtocolResponse> handshake(@RequestBody HandshakeRequest handshakeRequest) throws Exception {
        System.out.println("[Debug] Received handshake request...");
        String certificate = handshakeRequest.getCertificate();

        try {
            boolean valid = customProtocol.verifyCertificate(certificate, PROJECT_PATH + "certificates/myCA.crt", "hospital");
            System.out.println("Certificate is " + valid);
            String hospitalPubKey = handshakeRequest.getHospitalPubKey();

            String diffieLabKey = customProtocol.diffieLabPublicKey(hospitalPubKey);
            customProtocol.generateSharedSecret(hospitalPubKey);


            //encrypted random string
//            String randomString = customProtocol.createRandomString(certificate);
            String nonce = customProtocol.createNonce();
            HandshakeResponse handshakeResponse = new HandshakeResponse(diffieLabKey, nonce);

            // Using mapper to transform testResponse into string
            // Doing mac of the resulting string, generating the data string meant to put in customProtocolResponse
            ObjectMapper mapper = new ObjectMapper();
            String respData = mapper.writeValueAsString(handshakeResponse);
            String mac = customProtocol.macMessage(respData.getBytes());

            // mac = tag + respData (json string -> handshakeResponse)
            CustomProtocolResponse response = new CustomProtocolResponse(mac, "");

            System.out.println("[Debug] Sending handshake response...");
            return ResponseEntity.ok(response);

        } catch (InvalidAlgorithmParameterException | FileNotFoundException | NoSuchAlgorithmException e) {
            System.out.println("Handshake failed");
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PostMapping("/teststoanalyze")
    public ResponseEntity<CustomProtocolResponse> testsToAnalyze(@RequestBody ProtectedTestRequest testreq) throws NoSuchAlgorithmException, IOException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        System.out.println("[Debug] Received tests results request (after handshake)...");

        //Generate secret key
        //customProtocol.generateSecretKey(hsResponse, PROJECT_PATH + "certificates/hospitalKeystore.jks");
        TestRequest req = testreq.getTestRequest();

        // Because for simplicity reasons we only answer to requests with id 1
        // id is only for representation purposes in case this was a real system
        // we would have multiple id's...
        int id = Integer.parseInt(customProtocol.decryptWithSecretKey(req.getId(), testreq.getIv()));
        if(id != 1)
            return ResponseEntity.status(404).build();

        if(customProtocol.dataCheck(testreq.getMac()) && customProtocol.verifyNonce(req.getNonce())) {

            // Encrypting test results with secret key
            String results = "25/05/2020 Covid19:True,Pneumonia:True...";
            String[] encryptResValues = customProtocol.encryptWithSecretKey(results);
            String encryptedResults = encryptResValues[0];

            // Object containing encrypted random string + encrypted test results + signature + nonce
            String signature = Crypto.signData(results);
            String nonce = customProtocol.createNonce();

            TestResponse resp = new TestResponse(encryptedResults, signature, nonce);

            // Using mapper to transform testResponse into string
            // Doing mac of the resulting string, generating the data string meant to put in customProtocolResponse
            ObjectMapper mapper = new ObjectMapper();
            String respData = mapper.writeValueAsString(resp);

            String mac = customProtocol.macMessage(respData.getBytes());

            CustomProtocolResponse response = new CustomProtocolResponse(mac, encryptResValues[1]);

            if(signature.equals(""))
                return ResponseEntity.status(500).build();

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(500).build();
    }
}
