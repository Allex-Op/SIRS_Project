package sirs.api.lab;

import sirs.api.lab.entities.CustomProtocolResponse;
import sirs.api.lab.entities.TestResponse;

//TODO: This is just a possible sketch
public class CustomProtocol {
    String sessionKey;  //TODO: Esta session key devem receber do hospital ou gerar aqui, decidam
                        // como acharem melhor... se receberem do hospital talvez adicionar um endpoint
                        // especifico para isso?

    public String encryptData(TestResponse plaintextData) {
        //TODO: Use the generated session key to encrypt the data adding Confidentiality,Integrity & Freshness
        String cipheredText = plaintextData.getResults() + plaintextData.getDigitalSignature();
        return cipheredText;
    }

    public String decryptData(CustomProtocolResponse cipheredData) {
        //TODO: Decrypt the data, verify integrity and freshness
        String decryptedData = "some data";
        return decryptedData;
    }

    public boolean verifyIntegrity(String data) {
        //TODO
        return true;
    }
}
