package sirs.api.lab;

import sirs.api.lab.entities.CustomProtocolResponse;
import sirs.api.lab.entities.TestResponse;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.file.CopyOption;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public boolean verifyCertificate(Certificate certToCheck, String trustedAnchor) throws FileNotFoundException, CertificateException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            List mylist = new ArrayList();
            mylist.add(certToCheck);

            CertPath cp = cf.generateCertPath(mylist);
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
}
