package sirs.api.lab.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestResponse {
    String results;
    String digitalSignature;
    String nonce;

    @JsonCreator
    public TestResponse(@JsonProperty("results") String results, @JsonProperty("digitalSignature") String digitalSignature, @JsonProperty("nonce") String nonce) {
        this.results = results;
        this.digitalSignature = digitalSignature;
        this.nonce = nonce;
    }

    public String getResults() {
        return results;
    }

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public void setResults(String results) {
        this.results = results;
    }

}
