package sirs.api.hospital.entities;

public class TestRequest {
    String data;
    String certificate;

    public TestRequest(String data, String certificate) {
        this.data = data;
        this.certificate = certificate;
    }

    public String getData() {
        return data;
    }

    public String getCertificate() { return certificate; }

    public void setData(String data) {
        this.data = data;
    }

    public void setCertificate(String certificate) { this.certificate = certificate; }
}
