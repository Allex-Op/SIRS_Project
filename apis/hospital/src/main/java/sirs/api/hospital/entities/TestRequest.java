package sirs.api.hospital.entities;

public class TestRequest {
    String data;
    String nonce;

    public TestRequest(String data, String nonce) {
        this.data = data;
        this.nonce = nonce;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
