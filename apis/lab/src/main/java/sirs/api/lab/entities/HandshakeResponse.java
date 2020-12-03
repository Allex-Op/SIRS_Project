package sirs.api.lab.entities;

public class HandshakeResponse {
    /**
     *
     * randomString -- encrypted in base64 and with the hospitals public key
     * nonce -- encrypted in base64
     * tag -- result of mac.doFinal, its encrypted in base64
     *
     **/

    String randomString;
    String nonce;

    public HandshakeResponse(String randomString, String nonce) {
        this.randomString = randomString;
        this.nonce = nonce;
    }

    public String getRandomString() { return randomString; }

    public void setRandomString(String randomString) { this.randomString = randomString; }

    public String getNonce() { return nonce; }

    public void setNonce(String nonce) { this.nonce = nonce; }
}
