package sirs.api.lab;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sirs.api.lab.entities.CustomProtocolResponse;
import sirs.api.lab.entities.TestRequest;
import sirs.api.lab.entities.TestResponse;

import java.net.http.HttpClient;

@RestController
public class Handlers {
    Crypto cr = new Crypto();
    CustomProtocol cp = new CustomProtocol();

    @PostMapping("/teststoanalyze/{id}")
    public ResponseEntity<TestResponse> testsToAnalyze(@PathVariable int id, @RequestBody TestRequest testreq) {
        // Because for simplicity reasons we only answer to requests with id 1
        // id is only for representation purposes in case this was a real system
        // we would have multiple id's...
        if(id != 1)
            return ResponseEntity.status(404).build();

        String data = testreq.getData();
        String certificate = testreq.getCertificate();

        System.out.println(data);
        System.out.println(certificate);

        //TODO: verify certificate -> generate random string -> encrypt it with the public key of the Lab -> send

        String results = "25/05/2020 Covid19:True,Pneumonia:True...";
        String signature = cr.signData(results);
        TestResponse resp = new TestResponse(results, signature);

//        if(signature.equals(""))
//            return ResponseEntity.status(500).build();

        //TODO: ADD CUSTOM SECURITY CHANNEL HERE
        String safeData = cp.encryptData(resp);

        return ResponseEntity.ok(resp);
    }
}
