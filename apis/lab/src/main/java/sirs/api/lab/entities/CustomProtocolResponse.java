package sirs.api.lab.entities;

public class CustomProtocolResponse {
    String data;    // This data should provide the confidentiality, integrity, freshness...
                    // Also this data is in Base64 format because binary data is not
                    // suitable to be transported over the HTTP protocol.

    String encryptedString;

    public CustomProtocolResponse(String data, String encryptedString) {
        this.data = data;
        this.encryptedString = encryptedString;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setEncryptedString(String encryptedString) { this.encryptedString = encryptedString; }

    public String getEncryptedString() { return encryptedString; }
}
