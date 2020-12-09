package sirs.api.hospital;

import sirs.api.hospital.messages.HandshakeResponse;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.*;
import java.util.*;


public class CustomProtocol {
    private SecretKey secretKey;
    private String nonce;
    private final ArrayList<String> receivedNonces  = new ArrayList<String>();

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
        return  Base64.getEncoder().encodeToString(nonce.getBytes());

    }


    public boolean verifyNonce(String nonce) {
        String decnonce = Base64.getDecoder().decode(nonce).toString();

        boolean received = receivedNonces.contains(decnonce);
        if(!received) {
            receivedNonces.add(decnonce);
            return true;
        }else
            return false;

    }


}

