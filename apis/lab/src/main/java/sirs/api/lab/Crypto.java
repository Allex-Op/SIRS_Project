package sirs.api.lab;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class Crypto {
    private static final String SIGNATURE = "SHA256WithRSA";

    /**
     *  Used to digitally sign the test results,
     *  this function result is sent in the response when an hospital
     *  requests for test results data.
     */
    public static String signData(String data) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE);
            SecureRandom secureRandom = new SecureRandom();

            PrivateKey privKey = privateKeyReader("vagrant/examples/certificates/lab1.key");
            signature.initSign(privKey, secureRandom);
            signature.update(data.getBytes("UTF-8"));
            byte[] digitalSignature = signature.sign();
            String digitalSign = new String(digitalSignature);

            return digitalSign;

        } catch(Exception e) {
            System.out.println("Something went wrong while signing the data...");
            return null;
        }
    }

    public static PrivateKey privateKeyReader(String filename) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePrivate(spec);
    }
}
