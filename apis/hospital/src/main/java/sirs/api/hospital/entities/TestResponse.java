package sirs.api.hospital.entities;

public class TestResponse {
    String results;
    String digitalSignature;
    String encriptedString;
    
    public String getResults() {
        return results;
    }

    public String getEncriptedString() { return encriptedString; }

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
