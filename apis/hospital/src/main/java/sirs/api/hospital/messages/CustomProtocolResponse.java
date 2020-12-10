package sirs.api.hospital.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public class CustomProtocolResponse {
    /**
     *
     * String mac:
     *      This string is a result of using mac algorithm on a data string (that can
     *      be either a HandshakeResponse object or a TestResponse object).
     *      mac = Object + Tag
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

    @JsonIgnore
    public HandshakeResponse getHandshakeResponse() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] decodedMacBytes = Base64.getDecoder().decode(mac);
        byte[] handshakeResponse = Arrays.copyOfRange(decodedMacBytes, 0, decodedMacBytes.length - 32);

        return mapper.readValue(handshakeResponse, HandshakeResponse.class);
    }

    @JsonIgnore
    public TestResponse getTestResponse() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] decodedMacBytes = Base64.getDecoder().decode(mac);
        byte[] testResponse = Arrays.copyOfRange(decodedMacBytes, 0, decodedMacBytes.length - 32);

        return mapper.readValue(testResponse, TestResponse.class);
    }

}
