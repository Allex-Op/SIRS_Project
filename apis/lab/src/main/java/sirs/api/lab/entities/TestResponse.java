package sirs.api.lab.entities;

public class TestResponse {
    String results;
    String digitalSignature;
    String encryptedString;

    public TestResponse(String results, String digitalSignature, String encryptedString) {
        this.results = results;
        this.digitalSignature = digitalSignature;
        this.encryptedString = encryptedString;
    }

    public String getEncryptedString() { return encryptedString; }

    public String getResults() {
        return results;
    }

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public void setEncryptedString(String encryptedString) { this.encryptedString = encryptedString; }

    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
