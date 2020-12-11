package sirs.api.lab.entities;

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

    private String labKeyString;
    private String nonce;
    @JsonCreator
    public HandshakeResponse(@JsonProperty("labKeyString") String labKeyString, @JsonProperty("nonce") String nonce) {
        this.labKeyString = labKeyString;
        this.nonce = nonce;
    }

    public String getLabKeyString() { return labKeyString; }

    public void setLabKeyString(String randomString) { this.labKeyString = randomString; }

    public String getNonce() { return nonce; }

    public void setNonce(String nonce) { this.nonce = nonce; }
}
