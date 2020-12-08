package sirs.api.hospital.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestRequest {
    int id;
    String nonce;

    @JsonCreator
    public TestRequest(@JsonProperty("data") int id, @JsonProperty("nonce")  String nonce) {
        this.id = id;
        this.nonce = nonce;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getNonce() {
        return nonce;
    }

}
