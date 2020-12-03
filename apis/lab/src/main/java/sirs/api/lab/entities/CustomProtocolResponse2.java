package sirs.api.lab.entities;

public class CustomProtocolResponse2 {


    /**
     *
     * String data:
     *      This string is a result of using mapper on , transforming that object into a string.
     *      It is encrypted in base64, after applying macs algorithm on it.
     *
     **/
    private String mac;


    public CustomProtocolResponse2(String mac) {
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }



}
