package sirs.api.lab;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;

import javax.crypto.*;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class CustomProtocol {
   private  SecretKey secretKey;
   private SecretKeySpec bobAesKey;
   private SecretKeySpec aliceAesKey;
   private String nonce;
   private final ArrayList<String> receivedNonces  = new ArrayList<>();


    /**
     * *************************************
     * Start of Diffie Hellman's Functions  *
     * *************************************
     * */


    public String diffieLabPublicKey() throws NoSuchAlgorithmException, InvalidKeyException {
        /*
         * Alice creates her own DH key pair with 2048-bit key size
         */
        System.out.println("ALICE: Generate DH keypair ...");
        KeyPairGenerator aliceKpairGen = KeyPairGenerator.getInstance("DH");
        aliceKpairGen.initialize(2048);
        KeyPair aliceKpair = aliceKpairGen.generateKeyPair();

        // Alice creates and initializes her DH KeyAgreement object
        System.out.println("ALICE: Initialization ...");
        KeyAgreement aliceKeyAgree = KeyAgreement.getInstance("DH");
        aliceKeyAgree.init(aliceKpair.getPrivate());

        // Alice encodes her public key, and sends it over to Bob.
        byte[] alicePubKeyEnc = aliceKpair.getPublic().getEncoded();

        return Base64.getEncoder().encodeToString(alicePubKeyEnc);
    }

    public String diffieHospitalPublicKey(String alicepubkey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException {
        /*
         * Let's turn over to Bob. Bob has received Alice's public key
         * in encoded format.
         * He instantiates a DH public key from the encoded key material.
         *
         */
         byte [] alicePubKeyEnc= Base64.getDecoder().decode( alicepubkey);

        KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(alicePubKeyEnc);

        PublicKey alicePubKey = bobKeyFac.generatePublic(x509KeySpec);

        /*
         * Bob gets the DH parameters associated with Alice's public key.
         * He must use the same parameters when he generates his own key
         * pair.
         */
        DHParameterSpec dhParamFromAlicePubKey = ((DHPublicKey)alicePubKey).getParams();

        // Bob creates his own DH key pair
        System.out.println("BOB: Generate DH keypair ...");
        KeyPairGenerator bobKpairGen = KeyPairGenerator.getInstance("DH");
        bobKpairGen.initialize(dhParamFromAlicePubKey);
        KeyPair bobKpair = bobKpairGen.generateKeyPair();

        // Bob creates and initializes his DH KeyAgreement object
        System.out.println("BOB: Initialization ...");
        KeyAgreement bobKeyAgree = KeyAgreement.getInstance("DH");
        bobKeyAgree.init(bobKpair.getPrivate());

        // Bob encodes his public key, and sends it over to Alice.
        byte[] bobPubKeyEnc = bobKpair.getPublic().getEncoded();
        return Base64.getEncoder().encodeToString(bobPubKeyEnc);
    }

    
    public Key firstPhaseLab(String alicepubkey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException {
        /*
         * Alice uses Bob's public key for the first (and only) phase
         * of her version of the DH
         * protocol.
         * Before she can do so, she has to instantiate a DH public key
         * from Bob's encoded key material.
         */
        String bobpubkey = diffieHospitalPublicKey(alicepubkey);

        byte [] bobPubKeyEnc= Base64.getDecoder().decode(bobpubkey);

        KeyFactory aliceKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bobPubKeyEnc);
        PublicKey bobPubKey = aliceKeyFac.generatePublic(x509KeySpec);
        System.out.println("ALICE: Execute PHASE1 ...");
        KeyAgreement aliceKeyAgree = null;

        return aliceKeyAgree.doPhase(bobPubKey, true);
    }

    public Key firtPhaseHospital(String alicepubkey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        /*
         * Bob uses Alice's public key for the first (and only) phase
         * of his version of the DH
         * protocol.
         */
        byte [] alicePubKeyEnc= Base64.getDecoder().decode(alicepubkey);

        KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(alicePubKeyEnc);

        PublicKey alicePubKey = bobKeyFac.generatePublic(x509KeySpec);
        System.out.println("BOB: Execute PHASE1 ...");
        KeyAgreement bobKeyAgree = null;

        return bobKeyAgree.doPhase(alicePubKey, true);
    }

    /*
     * At this stage, both Alice and Bob have completed the DH key
     * agreement protocol.
     * Both generate the (same) shared secret.
     */
    public void generateSharedSecret(KeyAgreement aliceKeyAgree, KeyAgreement bobKeyAgree) throws Exception {
        byte[] aliceSharedSecret = aliceKeyAgree.generateSecret();
        int aliceLen = aliceSharedSecret.length;

        byte[] bobSharedSecret = new byte[aliceLen];

        int bobLen = bobKeyAgree.generateSecret(bobSharedSecret, 0);

        System.out.println("Alice secret: " + toHexString(aliceSharedSecret));
        System.out.println("Bob secret: " + toHexString(bobSharedSecret));

        if (!java.util.Arrays.equals(aliceSharedSecret, bobSharedSecret))
            throw new Exception("Shared secrets differ.");
        System.out.println("Shared secrets are the same.");


        /*
         * Now let's create a SecretKey object using the shared secret
         * and use it for encryption. First, we generate SecretKeys for the
         * "AES" algorithm (based on the raw shared secret data) and
         * Then we use AES in CBC mode, which requires an initialization
         * vector (IV) parameter. Note that you have to use the same IV
         * for encryption and decryption: If you use a different IV for
         * decryption than you used for encryption, decryption will fail.
         *
         * If you do not specify an IV when you initialize the Cipher
         * object for encryption, the underlying implementation will generate
         * a random one, which you have to retrieve using the
         * javax.crypto.Cipher.getParameters() method, which returns an
         * instance of java.security.AlgorithmParameters. You need to transfer
         * the contents of that object (e.g., in encoded format, obtained via
         * the AlgorithmParameters.getEncoded() method) to the party who will
         * do the decryption. When initializing the Cipher for decryption,
         * the (re-instantiated) AlgorithmParameters object must be explicitly
         * passed to the Cipher.init() method.
         */

        System.out.println("Use shared secret as SecretKey object ...");
        bobAesKey = new SecretKeySpec(bobSharedSecret, 0, 16, "AES");
        aliceAesKey = new SecretKeySpec(aliceSharedSecret, 0, 16, "AES");

        /*
         * Bob encrypts, using AES in CBC mode
         */
        Cipher bobCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        bobCipher.init(Cipher.ENCRYPT_MODE, bobAesKey);
        byte[] cleartext = "This is just an example".getBytes();
        byte[] ciphertext = bobCipher.doFinal(cleartext);

        // Retrieve the parameter that was used, and transfer it to Alice in
        // encoded format
        byte[] encodedParams = bobCipher.getParameters().getEncoded();

        /*
         * Alice decrypts, using AES in CBC mode
         */
        // Instantiate AlgorithmParameters object from parameter encoding
        // obtained from Bob
        AlgorithmParameters aesParams = AlgorithmParameters.getInstance("AES");
        aesParams.init(encodedParams);
        Cipher aliceCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aliceCipher.init(Cipher.DECRYPT_MODE, aliceAesKey, aesParams);
        byte[] recovered = aliceCipher.doFinal(ciphertext);

        if (!java.util.Arrays.equals(cleartext, recovered))
            throw new Exception("AES in CBC mode recovered text is " + "different from cleartext");
        System.out.println("AES in CBC mode recovered text is same as cleartext");
    }


    /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    /*
     * Converts a byte array to hex string
     */
    private static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (i < len-1) {
                buf.append(":");
            }
        }
        return buf.toString();
    }


    /**
     * ***********************************
     * End of Diffie Hellman's Functions  *
     * ***********************************
     * */


    public String createRandomString(String certificate) throws NoSuchPaddingException, NoSuchAlgorithmException, CertificateException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        // Generating random string
        byte[] randomString = new byte[32];
        new Random().nextBytes(randomString);

        // Generating secretKey from randomString
        secretKey = new SecretKeySpec(randomString, 0, randomString.length, "AES");

        // Encrypt random string with pub key
        return encryptRandomString(certificate, randomString);
    }

    public PublicKey extractPubKey(String certificate) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream cert = new ByteArrayInputStream(certificate.getBytes());
        Certificate final_certificate = cf.generateCertificate(cert);

        return final_certificate.getPublicKey();
    }

    public byte[] encryptData(byte[] randomString, PublicKey pubKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        // Encrypt the data adding Confidentiality, Integrity & Freshness
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        return cipher.doFinal(randomString);
    }

    public String encryptWithSecretKey(String stringToEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = null;
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8)));

    }

    public boolean verifyCertificate(String certificateToCheck, String trustedAnchor, String expectedCN) throws FileNotFoundException, CertificateException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        byte [] decoded = org.apache.tomcat.util.codec.binary.Base64.decodeBase64(certificateToCheck.replaceAll("-----BEGIN CERTIFICATE-----\n", "").replaceAll("-----END CERTIFICATE-----", ""));
        Certificate certToCheck = CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(decoded));

        //Verify CN
        X509Certificate c = (X509Certificate)certToCheck;
        X509Principal principal = PrincipalUtil.getSubjectX509Principal(c);
        Vector<?> subjectCNs = principal.getValues(X509Name.CN);

        if(subjectCNs.get(0).equals(expectedCN)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            List list = new ArrayList();
            list.add(certToCheck);

            CertPath cp = cf.generateCertPath(list);
            FileInputStream in = new FileInputStream(trustedAnchor);
            Certificate trust = cf.generateCertificate(in);
            TrustAnchor anchor = new TrustAnchor((X509Certificate) trust, null);
            PKIXParameters params = new PKIXParameters(Collections.singleton(anchor));
            params.setRevocationEnabled(false);
            CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
            PKIXCertPathValidatorResult result = null;

            try {
                result = (PKIXCertPathValidatorResult) cpv.validate(cp, params);
                return true;
            } catch (CertPathValidatorException e) {
                System.out.println("Invalid Certificate");
                return false;
            }
        } else
            return false;
    }

    public String macMessage(byte[] responseBytes) throws InvalidKeyException, NoSuchAlgorithmException {
        // Creating Mac object and initializing
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);

        byte[] response = Arrays.copyOfRange(responseBytes, 0, responseBytes.length);
        byte[] tag = mac.doFinal(response);
        byte[] message = new byte[response.length + tag.length];

        System.arraycopy(response, 0, message, 0, response.length);
        System.arraycopy(tag, 0, message, response.length, tag.length);

        return Base64.getEncoder().encodeToString(message);
    }

    public String encryptRandomString(String certificate, byte[] randomString) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, CertificateException {
        // Encrypt random string with pub key
        PublicKey pubKey = extractPubKey(certificate);
        byte[] encrypted_data = encryptData(randomString, pubKey);
        return java.util.Base64.getEncoder().encodeToString(encrypted_data);
    }

    public String createNonce() {
        byte[] randomString = new byte[32];
        new Random().nextBytes(randomString);
        nonce = new String(randomString )+ Long.toString(System.currentTimeMillis());
        return nonce;

    }

    public boolean dataCheck(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        // Creating Mac object and initializing
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(this.secretKey);

        byte[] decodedDataBytes = Base64.getDecoder().decode(data);

        byte[] message = Arrays.copyOfRange(decodedDataBytes, 0, decodedDataBytes.length - 32);
        byte[] tag = Arrays.copyOfRange(decodedDataBytes, decodedDataBytes.length - 32, decodedDataBytes.length);

        byte[] check_tag = mac.doFinal(message);

        if(Arrays.equals(check_tag, tag)) {
            return true;
        }
        System.out.println("Message not secure.");
        return false;
    }

    public boolean verifyNonce(String nonce) {
       boolean received = receivedNonces.contains(nonce);
       if(!received) {
           receivedNonces.add(nonce);
           return true;
       }else
           return false;
    }
}
