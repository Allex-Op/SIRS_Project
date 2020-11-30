package sirs.api.hospital.entities;

public class TestResponse {
    String results;
    String digitalSignature;

    // TODO: DELETE THE NEXT FIELD, IT DOES NOT EXIST NOW. THIS FIELD IS IN CUSTOM PROTOCOL RESPONSE
    String encryptedString;
    
    public String getResults() {
        return results;
    }

    public String getEncryptedString() { return encryptedString; }

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
