package sirs.api.hospital;

import sirs.api.hospital.entities.CustomProtocolResponse;
import sirs.api.hospital.entities.TestRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO: This is just a possible sketch
public class CustomProtocol {
    String sessionKey;

    public boolean initHandshake() {
        //TODO: Change certificates with HTTP requests
        // Verify if the certificate belongs to the LAB (important)
        // Generate random string and encrypt it with the HOSPITAL public key
        // Generate sessionKey here or on the lab & store it for further use...
        sessionKey = "superSecretKey";  // The type of the key won't be string btw, this is just an example
        return true;
    }

    public String encryptData(TestRequest plaintextData) {
        //TODO: Use the generated session key to encrypt the data adding Confidentiality,Integrity & Freshness
        String cipheredText = plaintextData.getData();
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

