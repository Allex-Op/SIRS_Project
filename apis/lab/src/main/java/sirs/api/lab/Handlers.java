package sirs.api.lab;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sirs.api.lab.entities.CustomProtocolResponse;
import sirs.api.lab.entities.TestRequest;
import sirs.api.lab.entities.TestResponse;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import static java.nio.file.Files.readAllBytes;

@RestController
public class Handlers {
    Crypto cr = new Crypto();
    CustomProtocol cp = new CustomProtocol();

    @PostMapping("/teststoanalyze/{id}")
    public ResponseEntity<TestResponse> testsToAnalyze(@PathVariable int id, @RequestBody TestRequest testreq) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, CertificateException {
        // Because for simplicity reasons we only answer to requests with id 1
        // id is only for representation purposes in case this was a real system
        // we would have multiple id's...
        if(id != 1)
            return ResponseEntity.status(404).build();

        String data = testreq.getData();
        String certificate = testreq.getCertificate();

        //TODO: verify certificate

        // Generating random string
        byte[] randomString = new byte[64];
        new Random().nextBytes(randomString);

        // Extract pub key from certificate
        PublicKey pubKey = cp.extractPubKey(certificate);

        // Encrypt random string with pub key
        byte[] encrypted_data = cp.encryptData(randomString, pubKey);
        String encripted_string = new String(encrypted_data);

        //TODO: send encrypted random string
        String results = "25/05/2020 Covid19:True,Pneumonia:True...";
        String signature = cr.signData(results);
        TestResponse resp = new TestResponse(results, signature, encripted_string);

//        if(signature.equals(""))
//            return ResponseEntity.status(500).build();


        return ResponseEntity.ok(resp);
    }
}
