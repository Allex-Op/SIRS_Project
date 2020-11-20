package sirs.api.hospital;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class crypto {
    public static String algo = "SHA256WithRSA";

    /**
     *  We will use the lab. associated with the test result public key
     *  to decode it's signature, the result will then be compared with the
     *  hash of the tests data, if the value is the same the signature is valid and true is returned.
     *
     *  http://tutorials.jenkov.com/java-cryptography/signature.html
     */
    public static boolean verifySignature(String data, String digitalSignature, String publicKey) {
        try {
            // Digital signature encoded in base64 for transport over HTTP
            byte[] decodedBytes = Base64.getDecoder().decode(digitalSignature);

            //Extracting public key and initializing
            PublicKey pubKey = extractPublicKey(publicKey);
            Signature signature = Signature.getInstance(algo);
            signature.initVerify(pubKey);

            //Feeding with the results tests data
            byte[] dataBytes = data.getBytes();
            signature.update(dataBytes);

            //Check signature
            return signature.verify(decodedBytes);
        } catch(Exception e) {
            System.out.println("Signature validation process failed...");
            return false;
        }
    }

    /**
     * Public key is encoded in PEM format, we must decode it back to the
     * DER format and use those bytes to create the public key object
     */
    private static PublicKey extractPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyPEM = publicKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }
}
