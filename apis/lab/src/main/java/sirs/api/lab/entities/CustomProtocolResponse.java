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

    @JsonCreator
    public CustomProtocolResponse(@JsonProperty("mac") String mac) {
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }

}
