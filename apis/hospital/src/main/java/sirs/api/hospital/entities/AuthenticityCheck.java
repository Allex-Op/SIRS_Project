package sirs.api.hospital.entities;

public class AuthenticityCheck {
    String check;

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public AuthenticityCheck(String check) {
        this.check = check;
    }
}
