package sirs.api.hospital;

import com.fasterxml.jackson.databind.ObjectMapper;
import sirs.api.hospital.entities.CustomProtocolResponse;
import sirs.api.hospital.entities.HandshakeResponse;
import sirs.api.hospital.entities.TestResponse;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;


public class CustomProtocol {

    public static byte[] decryptData(byte[] cipheredData, PrivateKey privKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // Decrypt the data, verify integrity and freshness
        Cipher decrypt=Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        decrypt.init(Cipher.DECRYPT_MODE, privKey);
        byte[] decryptedData = decrypt.doFinal(cipheredData);

        return decryptedData;
    }


    public static PrivateKey extractPrivKey(File keyStoreFile) throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
        KeyStore keystore = KeyStore.getInstance("JKS");
        String password = "hospital";
        FileInputStream is = new FileInputStream(keyStoreFile);
        String alias = "hospital";

        keystore.load(is, password.toCharArray());
        PrivateKey privKey = (PrivateKey) keystore.getKey(alias, password.toCharArray());

        return privKey;
    }

    public static String decryptWithSecretKey(String stringToDecrypt, SecretKey secretKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)));
    }

    public static TestResponse dataCheck(String data, SecretKey secretKey) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        // Creating Mac object and initializing
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        ObjectMapper mapper = new ObjectMapper();

        byte[] decodedDataBytes = Base64.getDecoder().decode(data);

        byte[] message = Arrays.copyOfRange(decodedDataBytes, 0, decodedDataBytes.length - 32);
        byte[] tag = Arrays.copyOfRange(decodedDataBytes, decodedDataBytes.length - 32, decodedDataBytes.length);

        byte[] check_tag = mac.doFinal(message);

        if(Arrays.equals(check_tag, tag)) {
            System.out.println("Tags are equal. Data received was not tampered.");
            TestResponse resp = mapper.readValue(message, TestResponse.class);
            return resp;
        }

        System.out.println("Message not secure.");
        return null;
    }

    public boolean verifyIntegrity(String data) {
        //TODO
        return true;
    }

    public static SecretKey generateSecretKey(HandshakeResponse message, String path) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        // Getting the encrypted random string from CustomProtocolResponse
        String encryptedString64 = message.getRandomString();
        byte[] encryptedStringBytes = Base64.getDecoder().decode(encryptedString64);

        // Extract private key from hospitalKeyStore
        File keyStoreFile = new File(path);
        PrivateKey privKey = extractPrivKey(keyStoreFile);

        // Decrypt random string received
        byte[] decryptedStringBytes = decryptData(encryptedStringBytes, privKey);

        // Generate secret key
        return new SecretKeySpec(decryptedStringBytes, 0, decryptedStringBytes.length, "AES");

    }
}

