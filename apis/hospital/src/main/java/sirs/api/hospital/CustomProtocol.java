package sirs.api.hospital;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;


public class CustomProtocol {
    private SecretKey secretKey;
    private KeyAgreement hospitalKeyAgree;
    private String nonce;
    private final ArrayList<String> receivedNonces  = new ArrayList<>();

    /**
     * *************************************
     * Start of Diffie Hellman's Functions  *
     * *************************************
     * */

    public String diffieHospitalPublicKey() throws NoSuchAlgorithmException, InvalidKeyException {
        // Hospital creates her own DH key pair with 2048-bit key size
        KeyPairGenerator hospitalKpairGen = KeyPairGenerator.getInstance("DH");
        hospitalKpairGen.initialize(2048);
        KeyPair hospitalKpair = hospitalKpairGen.generateKeyPair();

        // Hospital creates and initializes her DH KeyAgreement object
        hospitalKeyAgree = KeyAgreement.getInstance("DH");
        hospitalKeyAgree.init(hospitalKpair.getPrivate());

        // Hospital encodes her public key, and sends it over to lab.
        byte[] hospitalPubKeyEnc = hospitalKpair.getPublic().getEncoded();

        return Base64.getEncoder().encodeToString(hospitalPubKeyEnc);
    }

    public void firstPhaseHospital(String labpubkey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        /*
         * Hospital uses lab's public key for the first (and only) phase
         * of her version of the DH
         * protocol.
         * Before she can do so, she has to instantiate a DH public key
         * from lab's encoded key material.
         */
        byte [] labPubKeyEnc= Base64.getDecoder().decode(labpubkey);

        KeyFactory hospitalKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(labPubKeyEnc);
        PublicKey labPubKey = hospitalKeyFac.generatePublic(x509KeySpec);
        hospitalKeyAgree.doPhase(labPubKey, true);
    }

    /*
     * At this stage, both hospital and lab have completed the DH key
     * agreement protocol.
     * Both generate the (same) shared secret.
     */
    public void generateSharedSecret(String labpubkey) throws Exception {
        /*
         * Lab uses hospital's public key for the first (and only) phase
         * of his version of the DH protocol.
         */
        firstPhaseHospital(labpubkey);
        byte[] hospitalSharedSecret = hospitalKeyAgree.generateSecret();

        // Creating a SecretKey object using the shared secret and use it for encryption.
        SecretKeySpec hospitalAesKey = new SecretKeySpec(hospitalSharedSecret, 0, 16, "AES");
        secretKey = hospitalAesKey;
    }


    /**
     * ***********************************
     * End of Diffie Hellman's Functions  *
     * ***********************************
     * */


    public String decryptWithSecretKey(String stringToDecrypt) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
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
    public String encryptWithSecretKey(String stringToEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8)));

    }

}

