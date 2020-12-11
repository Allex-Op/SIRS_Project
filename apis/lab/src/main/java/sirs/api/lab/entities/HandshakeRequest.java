package sirs.api.lab.entities;

public class HandshakeRequest {
    String certificate;
    String hospitalPubKey;

    public HandshakeRequest(String certificate, String hospitalPubKey) {
        this.hospitalPubKey = hospitalPubKey;
        this.certificate = certificate;
    }

    public String getCertificate() { return certificate; }

    public void setCertificate(String certificate) { this.certificate = certificate; }

    public String getHospitalPubKey() { return hospitalPubKey; }

    public void setHospitalPubKey(String hospitalPubKey) { this.hospitalPubKey = hospitalPubKey; }
}
