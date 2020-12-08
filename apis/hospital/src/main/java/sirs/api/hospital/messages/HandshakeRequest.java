package sirs.api.hospital.messages;

public class HandshakeRequest {
    String certificate;

    public HandshakeRequest(String certificate) {
        this.certificate = certificate;
    }

    public String getCertificate() { return certificate; }

    public void setCertificate(String certificate) { this.certificate = certificate; }
}
