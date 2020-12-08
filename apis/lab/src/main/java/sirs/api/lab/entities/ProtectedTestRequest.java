package sirs.api.lab.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public class ProtectedTestRequest {


    /**
     *
     * String data:
     *      This string is a result of using mapper on , transforming that object into a string.
     *      It is encrypted in base64, after applying macs algorithm on it.
     *
     **/
    public String mac;

    @JsonCreator
    public ProtectedTestRequest(@JsonProperty("mac") String mac) {
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }


    public TestRequest getTestRequest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] decodedMacBytes = Base64.getDecoder().decode(mac);
        byte[] testRequest = Arrays.copyOfRange(decodedMacBytes, 0, decodedMacBytes.length - 32);

        return mapper.readValue(testRequest, TestRequest.class);
    }

}
