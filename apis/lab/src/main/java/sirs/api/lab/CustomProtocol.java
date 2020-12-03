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
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class CustomProtocol {
    SecretKey secretKey;

    public String createRandomString(String certificate) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, CertificateException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException {
        // Generating random string
        byte[] randomString = new byte[32];
        new Random().nextBytes(randomString);

        // Generating secretKey from randomString
        secretKey = new SecretKeySpec(randomString, 0, randomString.length, "AES");

        // Encrypt random string with pub key
        String encryptedString64 = encryptRandomString(certificate, randomString);

        return encryptedString64;
    }

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

    public String encryptWithSecretKey(String stringToEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = null;
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(stringToEncrypt.getBytes("UTF-8")));

    }

    public boolean verifyCertificate(Certificate certToCheck, String trustedAnchor, String expectedCN) throws FileNotFoundException, CertificateException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        //Verify CN
        X509Certificate c = (X509Certificate)certToCheck;
        X509Principal principal = PrincipalUtil.getSubjectX509Principal(c);
        Vector<?> subjectCNs = principal.getValues(X509Name.CN);

        if(subjectCNs.get(0).equals("hospital")) {
            System.out.println("Correct CN " );


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

    public String macTwoStrings (String nonce, String randomString) throws InvalidKeyException, NoSuchAlgorithmException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);

        byte[] nonceB = nonce.getBytes(StandardCharsets.UTF_8);
        byte[] randomStringB = randomString.getBytes(StandardCharsets.UTF_8);
        byte[] message = new byte[nonceB.length + randomStringB.length];

        System.arraycopy(message, 0, nonceB, 0, nonceB.length);
        System.arraycopy(message, 0, randomStringB, nonceB.length, randomStringB.length);

        byte[] tag = mac.doFinal(message);
        String tag64 = Base64.getEncoder().encodeToString(tag);

        return tag64;
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
        String message64 = Base64.getEncoder().encodeToString(message);

        return message64;
    }

    public String encryptRandomString(String certificate, byte[] randomString) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException, CertificateException {
        // Encrypt random string with pub key
        PublicKey pubKey = extractPubKey(certificate);
        byte[] encrypted_data = encryptData(randomString, pubKey);
        return java.util.Base64.getEncoder().encodeToString(encrypted_data);

    }

    public String createNonce() {
        byte[] randomString = new byte[32];
        new Random().nextBytes(randomString);
        String nonce = new String(randomString);
        return nonce;
    }

}
