package sirs.api.lab.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomProtocolResponse {
    /**
     *
     * String mac:
     *      This string is a result of using mac algorithm on a data string (that can
     *      be either a HandshakeResponse object or a TestResponse object).
     *
     **/
    public String mac;

    public String iv;

    @JsonCreator
    public CustomProtocolResponse(@JsonProperty("mac") String mac, String iv) {
        this.mac = mac;
        this.iv = iv;
    }

    public String getMac() {
        return mac;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}
