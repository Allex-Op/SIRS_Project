package sirs.api.hospital.entities;

public class CustomProtocolResponse {
    String data;    // This data should provide the confidentiality, integrity, freshness...
    // Also this data should be in Base64 format cause binary data is not
    // suitable to be transported over the HTTP protocol.

    public CustomProtocolResponse(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
