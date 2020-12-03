package sirs.api.lab;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sirs.api.lab.entities.*;

import javax.crypto.*;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;

@RestController
public class Handlers {
    CustomProtocol customProtocol = new CustomProtocol();


    //TODO:
    // return nonce, randomString, mac (nonce + secret key)

    @PostMapping("/beginhandshake")
    public ResponseEntity<HandshakeResponse> handshake(@RequestBody HandshakeRequest handshakeRequest) throws CertificateException, NoSuchPaddingException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException {
        String certificate = handshakeRequest.getCertificate();

        byte [] decoded = Base64.decodeBase64(certificate.replaceAll("-----BEGIN CERTIFICATE-----\n", "").replaceAll("-----END CERTIFICATE-----", ""));
        Certificate cert = CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(decoded));
        try {
            boolean valid = customProtocol.verifyCertificate(cert, "src/main/resources/myCA.crt", "hospital");
            System.out.println("Certificate is " + valid);

            String randomString = customProtocol.createRandomString(certificate);
            String nonce = customProtocol.createNonce();

            // Concatenating randomString with nonce, generates a tag with mac
            String tag = customProtocol.macTwoStrings(randomString, nonce);

            HandshakeResponse handshakeResponse = new HandshakeResponse(randomString, nonce, tag);

            return ResponseEntity.ok(handshakeResponse);

        } catch (InvalidAlgorithmParameterException | FileNotFoundException | NoSuchAlgorithmException e) {
            System.out.println("Ups NOT WORKING");
            return null;
        }
    }

    @PostMapping("/teststoanalyze/{id}")
    public ResponseEntity<CustomProtocolResponse> testsToAnalyze(@PathVariable int id, @RequestBody TestRequest testreq) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, CertificateException {
        // Because for simplicity reasons we only answer to requests with id 1
        // id is only for representation purposes in case this was a real system
        // we would have multiple id's...
        if(id != 1)
            return ResponseEntity.status(404).build();

        // Encrypting test results with secret key
        String results = "25/05/2020 Covid19:True,Pneumonia:True...";
        String encryptedResults = customProtocol.encryptWithSecretKey(results);

        // Object containing encrypted random string + encrypted test results + signature
        String signature = Crypto.signData(results);

        TestResponse resp = new TestResponse(encryptedResults, signature);

        // Using mapper to transform testResponse into string
        // Doing mac of the resulting string, generating the data string meant to put in customProtocolResponse
        ObjectMapper mapper = new ObjectMapper();
        String respData = mapper.writeValueAsString(resp);

        String data = customProtocol.macMessage(respData.getBytes());
        String nonce = customProtocol.createNonce();

        // nonce is not encrypted in base 64 whatsoever
        String tag = customProtocol.macTwoStrings(data, nonce);

        CustomProtocolResponse response = new CustomProtocolResponse(data, nonce, tag);

        if(signature.equals(""))
            return ResponseEntity.status(500).build();

        return ResponseEntity.ok(response);
    }
}
