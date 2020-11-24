package sirs.api.hospital;

import sirs.api.hospital.entities.TestRequest;

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
}

