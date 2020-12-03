package sirs.api.hospital.entities;

public class CustomProtocolResponse {
    /**
     *
     * String data:
     *      This string is a result of using mapper on testResponse, transforming that object into a string.
     *      It is encrypted in base64, after applying macs algorithm on it.
     *
     **/
    String data;
    String nonce;
    String tag;

    public CustomProtocolResponse(String data, String nonce, String tag) {
        this.data = data;
        this.nonce = nonce;
        this.tag = tag;
    }

    public String getData() { return data; }

    public String getNonce() { return nonce; }

    public String getTag() { return tag; }

}
