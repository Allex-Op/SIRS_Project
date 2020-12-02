package sirs.api.lab.entities;

public class HandshakeResponse {
    String encryptedRandomStringWithNonce;

    public HandshakeResponse(String encryptedRandomStringWithNonce) {
        this.encryptedRandomStringWithNonce = encryptedRandomStringWithNonce;
    }

    public String getRandomString() { return encryptedRandomStringWithNonce; }

    public void setRandomString(String randomString) { this.encryptedRandomStringWithNonce = encryptedRandomStringWithNonce; }
}
