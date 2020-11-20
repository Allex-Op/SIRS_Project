package sirs.api.hospital.entities;

public class TestResults {
    String results;
    String digitalSignature;

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }
}
