package sirs.api.lab.entities;

public class CustomProtocolResponse {
    String data;    // This data should provide the confidentiality, integrity, freshness...
                    // Also this data is in Base64 format because binary data is not
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
