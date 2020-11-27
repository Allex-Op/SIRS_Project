package sirs.api.lab;

import sirs.api.lab.entities.CustomProtocolResponse;
import sirs.api.lab.entities.TestResponse;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.file.CopyOption;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static java.nio.file.Files.readAllBytes;

//TODO: This is just a possible sketch
public class CustomProtocol {
    String sessionKey;  //TODO: Esta session key devem receber do hospital ou gerar aqui, decidam
                        // como acharem melhor... se receberem do hospital talvez adicionar um endpoint
                        // especifico para isso?

    public PublicKey extractPubKey(String certificate) throws CertificateException {

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream cert = new ByteArrayInputStream(certificate.getBytes());
        Certificate final_certificate = cf.generateCertificate(cert);
        PublicKey pubKey = final_certificate.getPublicKey();

        return pubKey;
    }

    public byte[] encryptData(byte[] randomString, PublicKey pubKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, BadPaddingException, IllegalBlockSizeException {

        //TODO: Encrypt the data adding Confidentiality, Integrity & Freshness
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] cipheredText = cipher.doFinal(randomString);

        return cipheredText;
    }

    public String decryptData(CustomProtocolResponse cipheredData) {
        //TODO: Decrypt the data, verify integrity and freshness
        String decryptedData = "some data";
        return decryptedData;
    }

    public boolean verifyIntegrity(String data) {
        //TODO
        return true;
    }
}
