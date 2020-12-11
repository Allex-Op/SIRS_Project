package sirs.api.lab;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Crypto {
    private static final String SIGNATURE = "SHA256WithRSA";
    private static final String PROJECT_PATH = System.getenv("PROJECT_PATH");

    /**
     *  Used to digitally sign the test results,
     *  this function result is sent in the response when an hospital
     *  requests for test results data.
     */
    public static String signData(String data) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE);
            SecureRandom secureRandom = new SecureRandom();

            File file = new File(PROJECT_PATH + "certificates/private.der");
            PrivateKey privKey = extractPrivKey(file);
            signature.initSign(privKey, secureRandom);
            signature.update(data.getBytes("UTF-8"));
            byte[] digitalSignature = signature.sign();
            //String digitalSign = new String(digitalSignature);

            return  Base64.getEncoder().encodeToString(digitalSignature);

        } catch(Exception e) {
            System.out.println("Something went wrong while signing the data...");
            return "";
        }
    }

    public static PrivateKey extractPrivKey(File file) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(file.getPath()));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

}
