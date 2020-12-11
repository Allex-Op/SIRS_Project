package sirs.api.lab;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;

import javax.crypto.*;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
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
   private SecretKey secretKey;
   private KeyAgreement labKeyAgree;
   private String nonce;
   private final ArrayList<String> receivedNonces  = new ArrayList<>();

    /**
     * *************************************
     * Start of Diffie Hellman's Functions  *
     * *************************************
     * */


    public String diffieLabPublicKey(String hospitalpubkey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException {
        /*
        * Let's turn over to lab. Lab has received hospital's public key
        * in encoded format.
        * He instantiates a DH public key from the encoded key material.
        *
        */
        byte [] hospitalPubKeyEnc= Base64.getDecoder().decode( hospitalpubkey);
        KeyFactory labKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(hospitalPubKeyEnc);
        PublicKey hospitalPubKey = labKeyFac.generatePublic(x509KeySpec);

        /*
         * Lab gets the DH parameters associated with hospital's public key.
         * He must use the same parameters when he generates his own key
         * pair.
         */
        DHParameterSpec dhParamFromhospitalPubKey = ((DHPublicKey) hospitalPubKey).getParams();

        // Lab creates his own DH key pair
        KeyPairGenerator labKpairGen = KeyPairGenerator.getInstance("DH");
        labKpairGen.initialize(dhParamFromhospitalPubKey);
        KeyPair labKpair = labKpairGen.generateKeyPair();

        // Lab creates and initializes his DH KeyAgreement object
        labKeyAgree = KeyAgreement.getInstance("DH");
        labKeyAgree.init(labKpair.getPrivate());

        // Lab encodes his public key, and sends it over to hospital.
        byte[] labPubKeyEnc = labKpair.getPublic().getEncoded();
        return Base64.getEncoder().encodeToString(labPubKeyEnc);
    }


    public void firstPhaseLab(String hospitalpubkey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        /*
         * Lab uses hospital's public key for the first (and only) phase
         * of his version of the DH protocol.
         */
        byte [] hospitalPubKeyEnc= Base64.getDecoder().decode(hospitalpubkey);

        KeyFactory labKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(hospitalPubKeyEnc);
        PublicKey hospitalPubKey = labKeyFac.generatePublic(x509KeySpec);

        labKeyAgree.doPhase(hospitalPubKey, true);
    }

    /*
     * At this stage, both hospital and lab have completed the DH key
     * agreement protocol.
     * Both generate the (same) shared secret.
     */
    public void generateSharedSecret(String hospitalpubkey) throws Exception {
        firstPhaseLab(hospitalpubkey);
        byte[] labSharedSecret = labKeyAgree.generateSecret();

        /*
         * Now let's create a SecretKey object using the shared secret
         * and use it for encryption.
         */
        secretKey = new SecretKeySpec(labSharedSecret, 0, 16, "AES");
    }


    /**
     * ***********************************
     * End of Diffie Hellman's Functions  *
     * ***********************************
     * */

    public String[] encryptWithSecretKey(String stringToEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        SecureRandom randomSecureRandom = new SecureRandom();
        byte[] iv = new byte[cipher.getBlockSize()];
        randomSecureRandom.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        String ivString = Base64.getEncoder().encodeToString(iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
        return new String[] { Base64.getEncoder().encodeToString(cipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8))), ivString };
    }

    public String decryptWithSecretKey(String stringToDecrypt, String iv) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        IvParameterSpec ivParams = new IvParameterSpec(Base64.getDecoder().decode(iv));
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey, ivParams);
        return new String(cipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)));
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

    public String createNonce() {
        byte[] randomString = new byte[32];
        new Random().nextBytes(randomString);
        nonce = new String(randomString )+ Long.toString(System.currentTimeMillis());

        return  Base64.getEncoder().encodeToString(nonce.getBytes());
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
