package sirs.api.lab.entities;

public class TestResponse {
    String results;
    String digitalSignature;
    String nonce;

    public TestResponse(String results, String digitalSignature, String nonce) {
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
