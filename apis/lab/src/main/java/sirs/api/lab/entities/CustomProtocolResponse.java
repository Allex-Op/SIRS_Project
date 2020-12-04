package sirs.api.lab.entities;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public class CustomProtocolResponse {
    /**
     *
     *
     **/
    private String mac;

    public CustomProtocolResponse(String mac) {
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }
    public TestResponse getTestResponse() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] decodedMacBytes = Base64.getDecoder().decode(mac);
        byte[] testResponse = Arrays.copyOfRange(decodedMacBytes, 0, decodedMacBytes.length - 32);

        return mapper.readValue(testResponse, TestResponse.class);
    }
}
