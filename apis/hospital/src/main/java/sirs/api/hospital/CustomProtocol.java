package sirs.api.hospital;

import sirs.api.hospital.messages.HandshakeResponse;
import javax.crypto.*;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;


public class CustomProtocol {
    private SecretKey secretKey;
    private KeyAgreement aliceKeyAgree;
    private String nonce;
    private final ArrayList<String> receivedNonces  = new ArrayList<>();

    /**
     * *************************************
     * Start of Diffie Hellman's Functions  *
     * *************************************
     * */


    public String diffieHospitalPublicKey() throws NoSuchAlgorithmException, InvalidKeyException {
        /*
         * Alice creates her own DH key pair with 2048-bit key size
         */
        System.out.println("ALICE: Generate DH keypair ...");
        KeyPairGenerator aliceKpairGen = KeyPairGenerator.getInstance("DH");
        aliceKpairGen.initialize(2048);
        KeyPair aliceKpair = aliceKpairGen.generateKeyPair();

        // Alice creates and initializes her DH KeyAgreement object
        System.out.println("ALICE: Initialization ...");
        aliceKeyAgree = KeyAgreement.getInstance("DH");
        aliceKeyAgree.init(aliceKpair.getPrivate());

        // Alice encodes her public key, and sends it over to Bob.
        byte[] alicePubKeyEnc = aliceKpair.getPublic().getEncoded();

        return Base64.getEncoder().encodeToString(alicePubKeyEnc);
    }

    public Key firstPhaseLab(String bobpubkey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        /*
         * Alice uses Bob's public key for the first (and only) phase
         * of her version of the DH
         * protocol.
         * Before she can do so, she has to instantiate a DH public key
         * from Bob's encoded key material.
         */
        byte [] bobPubKeyEnc= Base64.getDecoder().decode(bobpubkey);
        KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bobPubKeyEnc);
        PublicKey alicePubKey = bobKeyFac.generatePublic(x509KeySpec);

        KeyFactory aliceKeyFac = KeyFactory.getInstance("DH");
        x509KeySpec = new X509EncodedKeySpec(bobPubKeyEnc);
        PublicKey bobPubKey = aliceKeyFac.generatePublic(x509KeySpec);
        System.out.println("ALICE: Execute PHASE1 ...");
        KeyAgreement aliceKeyAgree = null;
        return aliceKeyAgree.doPhase(bobPubKey, true);
    }

    /*
     * At this stage, both Alice and Bob have completed the DH key
     * agreement protocol.
     * Both generate the (same) shared secret.
     */
    public void generateSharedSecret(String bobpubkey, String alicepubkey) throws Exception {
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
        bobKeyAgree.doPhase(alicePubKey, true);
        
        byte[] aliceSharedSecret = bobKeyAgree.generateSecret();
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
        SecretKeySpec bobAesKey = new SecretKeySpec(bobSharedSecret, 0, 16, "AES");
        SecretKeySpec aliceAesKey = new SecretKeySpec(aliceSharedSecret, 0, 16, "AES");
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


    public byte[] decryptData(byte[] cipheredData, PrivateKey privKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // Decrypt the data, verify integrity and freshness
        Cipher decrypt=Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        decrypt.init(Cipher.DECRYPT_MODE, privKey);
        return decrypt.doFinal(cipheredData);
    }

    public PrivateKey extractPrivKey(File keyStoreFile) throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
        KeyStore keystore = KeyStore.getInstance("JKS");
        String password = "hospital";
        FileInputStream is = new FileInputStream(keyStoreFile);
        String alias = "hospital";

        keystore.load(is, password.toCharArray());
        return (PrivateKey) keystore.getKey(alias, password.toCharArray());
    }

    public String decryptWithSecretKey(String stringToDecrypt) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)));
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



    public void generateSecretKey(HandshakeResponse message, String path) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        // Getting the encrypted random string from CustomProtocolResponse
        String encryptedString64 = message.getRandomString();
        byte[] encryptedStringBytes = Base64.getDecoder().decode(encryptedString64);

        // Extract private key from hospitalKeyStore
        File keyStoreFile = new File(path);
        PrivateKey privKey = extractPrivKey(keyStoreFile);

        // Decrypt random string received
        byte[] decryptedStringBytes = decryptData(encryptedStringBytes, privKey);

        this.secretKey = new SecretKeySpec(decryptedStringBytes, 0, decryptedStringBytes.length, "AES");
    }

    public String createNonce() {
        byte[] randomString = new byte[32];
        new Random().nextBytes(randomString);
        nonce = new String(randomString )+ Long.toString(System.currentTimeMillis());
        return nonce;

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

