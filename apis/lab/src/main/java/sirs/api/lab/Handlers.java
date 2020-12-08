package sirs.api.lab;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

@RestController
public class Handlers {
    CustomProtocol customProtocol = null;

    // Return nonce, randomString, mac (nonce + secret key)
    @PostMapping("/beginhandshake")
    public ResponseEntity<CustomProtocolResponse> handshake(@RequestBody HandshakeRequest handshakeRequest) throws CertificateException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException, JsonProcessingException {
        customProtocol = new CustomProtocol();
        String certificate = handshakeRequest.getCertificate();

        try {
            boolean valid = customProtocol.verifyCertificate(certificate, "src/main/resources/myCA.crt", "hospital");
            System.out.println("Certificate is " + valid);

            //encrypted random string
            String randomString = customProtocol.createRandomString(certificate);
            String nonce = customProtocol.createNonce();

            HandshakeResponse handshakeResponse = new HandshakeResponse(randomString, nonce);

            // Using mapper to transform testResponse into string
            // Doing mac of the resulting string, generating the data string meant to put in customProtocolResponse
            ObjectMapper mapper = new ObjectMapper();
            String respData = mapper.writeValueAsString(handshakeResponse);
            String mac = customProtocol.macMessage(respData.getBytes());

            // mac = tag + respData (json string -> handshakeResponse)
            CustomProtocolResponse response = new CustomProtocolResponse(mac);

            return ResponseEntity.ok(response);

        } catch (InvalidAlgorithmParameterException | FileNotFoundException | NoSuchAlgorithmException e) {
            System.out.println("Something is wrong, not working.");
            return null;
        }
    }

    @PostMapping("/teststoanalyze")
    public ResponseEntity<CustomProtocolResponse> testsToAnalyze(@RequestBody ProtectedTestRequest testreq) throws NoSuchAlgorithmException, IOException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        //Generate secret key
        //customProtocol.generateSecretKey(hsResponse, "src/main/resources/hospitalKeystore.jks");
        TestRequest req = testreq.getTestRequest();

        // Because for simplicity reasons we only answer to requests with id 1
        // id is only for representation purposes in case this was a real system
        // we would have multiple id's...

        if(req.getId() != 1)
            return ResponseEntity.status(404).build();

        if(customProtocol.dataCheck(testreq.getMac()) && customProtocol.verifyNonce(req.getNonce())) {

            // Encrypting test results with secret key
            String results = "25/05/2020 Covid19:True,Pneumonia:True...";
            String encryptedResults = customProtocol.encryptWithSecretKey(results);

            // Object containing encrypted random string + encrypted test results + signature + nonce
            String signature = Crypto.signData(results);
            String nonce = customProtocol.createNonce();

            TestResponse resp = new TestResponse(encryptedResults, signature, nonce);

            // Using mapper to transform testResponse into string
            // Doing mac of the resulting string, generating the data string meant to put in customProtocolResponse
            ObjectMapper mapper = new ObjectMapper();
            String respData = mapper.writeValueAsString(resp);

            String mac = customProtocol.macMessage(respData.getBytes());

            CustomProtocolResponse response = new CustomProtocolResponse(mac);

            if(signature.equals(""))
                return ResponseEntity.status(500).build();

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(500).build();
    }
}
