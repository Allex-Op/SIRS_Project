package sirs.api.hospital;

import org.springframework.http.ResponseEntity;
import sirs.api.hospital.entities.CustomProtocolResponse;
import sirs.api.hospital.entities.TestRequest;
import sirs.api.hospital.entities.TestResponse;

import javax.crypto.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

//TODO: This is just a possible sketch
public class CustomProtocol {
    String sessionKey;

    public byte[] decryptData(byte[] cipheredData, PrivateKey privKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // Decrypt the data, verify integrity and freshness
        Cipher decrypt=Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        decrypt.init(Cipher.DECRYPT_MODE, privKey);
        byte[] decryptedData = decrypt.doFinal(cipheredData);

        return decryptedData;
    }

    public PublicKey extractPubKey(String certificate) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream cert = new ByteArrayInputStream(certificate.getBytes());
        Certificate final_certificate = cf.generateCertificate(cert);
        PublicKey pubKey = final_certificate.getPublicKey();

        return pubKey;
    }

    public PrivateKey extractPrivKey(File keyStoreFile) throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
        KeyStore keystore = KeyStore.getInstance("JKS");
        String password = "hospital";
        FileInputStream is = new FileInputStream(keyStoreFile);
        String alias = "hospital";

        keystore.load(is, password.toCharArray());
        PrivateKey privKey = (PrivateKey) keystore.getKey(alias, password.toCharArray());

        return privKey;
    }

    public String decryptWithSecretKey(String stringToDecrypt, SecretKey secretKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)));
    }

    public String decodingFromBase64(String data) {
        byte[] decodedData = Base64.getDecoder().decode(data);
        String finalData = new String(decodedData);
        return finalData;
    }

    public TestResponse extractResponse(String message) throws IOException, ClassNotFoundException {
        String messageDecoded64 = decodingFromBase64(message);
        byte[] messageBytes = messageDecoded64.getBytes();

        byte[] response = Arrays.copyOfRange(messageBytes, 0, messageBytes.length - 32);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(response));
        TestResponse resp = (TestResponse) in.readObject();
        in.close();

        return resp;
    }

    public boolean verifyIntegrity(String data) {
        //TODO
        return true;
    }
}

