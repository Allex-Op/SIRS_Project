package sirs.api.lab;

import sirs.api.lab.entities.CustomProtocolResponse;
import sirs.api.lab.entities.TestRequest;
import sirs.api.lab.entities.TestResponse;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.CopyOption;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

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

        // Encrypt the data adding Confidentiality, Integrity & Freshness
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


    public String encryptWithSecretKey(String stringToEncrypt, SecretKey secretKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            return Base64.getEncoder().encodeToString(cipher.doFinal(stringToEncrypt.getBytes("UTF-8")));
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String encodingInBase64(byte[] data) {
        byte[] data_base64 = org.apache.tomcat.util.codec.binary.Base64.encodeBase64(data);
        String data_string64 = new String(data_base64);
        return data_string64;
    }

    public boolean verifyCertificate(Certificate certToCheck, String trustedAnchor) throws FileNotFoundException, CertificateException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {

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
    }

    public String macMessage(byte[] responseBytes, SecretKey secretKey) throws InvalidKeyException, NoSuchAlgorithmException {
        // Creating Mac object and initializing
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);

        byte[] response = Arrays.copyOfRange(responseBytes, 0, responseBytes.length);
        byte[] tag = mac.doFinal(response);
        byte[] message = new byte[response.length + tag.length];

        System.arraycopy(response, 0, message, 0, response.length);
        System.arraycopy(tag, 0, message, response.length, tag.length);

        String message64 = encodingInBase64(message);

        return message64;
    }

    public boolean verifyIntegrity(String data) {
        //TODO
        return true;
    }
}
