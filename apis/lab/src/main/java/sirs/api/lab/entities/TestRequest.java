package sirs.api.lab.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestRequest {
    String id;
    String nonce;

    @JsonCreator
    public TestRequest(@JsonProperty("data") String id, @JsonProperty("nonce")  String nonce) {
        this.id = id;
        this.nonce = nonce;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getNonce() {
        return nonce;
    }

}
