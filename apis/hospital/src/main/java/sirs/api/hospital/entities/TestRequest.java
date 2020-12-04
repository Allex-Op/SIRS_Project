package sirs.api.hospital.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestRequest {
    String data;



    String nonce;

    @JsonCreator
    public TestRequest(@JsonProperty("data") String data, @JsonProperty("nonce")  String nonce) {
        this.data = data;
        this.nonce = nonce;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public String getNonce() {
        return nonce;
    }

}
