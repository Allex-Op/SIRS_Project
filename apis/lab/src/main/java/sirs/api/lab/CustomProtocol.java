package sirs.api.lab;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;

public class CustomProtocol {
   private  SecretKey secretKey;
   private String nonce;
   private final ArrayList<String> receivedNonces  = new ArrayList<String>();

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
