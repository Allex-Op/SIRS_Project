package sirs.api.hospital.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HandshakeResponse {
    /**
     *
     * randomString -- encrypted in base64 and with the hospitals public key
     * nonce -- encrypted in base64
     * tag -- result of mac.doFinal, its encrypted in base64
     *
     **/

    private String randomString;
    private String nonce;
    @JsonCreator
    public HandshakeResponse(@JsonProperty("randomString") String randomString, @JsonProperty("nonce") String nonce) {
        this.randomString = randomString;
        this.nonce = nonce;
    }

    public String getRandomString() { return randomString; }

    public void setRandomString(String randomString) { this.randomString = randomString; }

    public String getNonce() { return nonce; }

    public void setNonce(String nonce) { this.nonce = nonce; }
}
