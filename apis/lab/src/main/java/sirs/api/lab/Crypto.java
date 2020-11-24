package sirs.api.lab;

import java.security.Signature;

public class Crypto {
    private String algo = "SHA256WithRSA";

    /**
     *  Used to digitally sign the test results,
     *  this function result is sent in the response when an hospital
     *  requests for test results data.
     */
    public String signData(String data) {
        try {
            //TODO: Read the private key from a keyStore, for that you first have to find out
            //TODO: how to add key to privateStore...
            //TODO: (ps important: use the algorithm specified in the algo variable)
            Signature signature = Signature.getInstance(algo);
            return "";
        } catch(Exception e) {
            System.out.println("Something went wrong while signing the data...");
            return "";
        }
    }
}
