package sirs.api.hospital;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sirs.api.hospital.entities.Patient;
import sirs.api.hospital.entities.AuthenticityCheck;
import sirs.api.hospital.entities.TestResults;
import sirs.api.hospital.entities.Testanalysis;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class endpoints {
    Repo repo = new Repo();

    @GetMapping("/patient/{id}/name")
    public ResponseEntity<Patient> getPatientName(@PathVariable int id) {
        Patient name = repo.getPatientName(id);
        if(name == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(name);
    }

    @GetMapping("/patient/{id}/diseases")
    public ResponseEntity<Patient> getPatientDiseases(@PathVariable int id) {
        Patient diseases = repo.getPatientDiseases(id);
        if(diseases == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(diseases);
    }

    @GetMapping("/patient/{id}/testresults")
    public ResponseEntity<Patient> getPatientTestResults(@PathVariable int id) {
        Patient results = repo.getPatientTestResults(id);
        if(results == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/patient/{id}/treatment")
    public ResponseEntity<Patient> getPatientTreatment(@PathVariable int id) {
        Patient treatment = repo.getPatientTreatment(id);
        if(treatment == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(treatment);
    }

    //TODO: Needs further testing
    @GetMapping("/checkauthenticity/{id}")
    public ResponseEntity<AuthenticityCheck> getTestAuthenticity(@PathVariable int id) {
        String[] info = repo.getResult(id);
        if(info[0] == null || info[1] == null || info[2] == null)
            return ResponseEntity.notFound().build();

        if(crypto.verifySignature(info[0], info[1], info[2]))
            return ResponseEntity.ok(new AuthenticityCheck("Test result is authentic!"));
        else
            return ResponseEntity.ok(new AuthenticityCheck("Test result test validity is compromised, do not use it."));
    }

    // Sends a bunch of tests data for analysis to the lab API
    @PostMapping("/sendtestresults")
    public ResponseEntity<String> sendTestToLab(@RequestBody Testanalysis testreq) {
        // Hardcoded values for demonstration purposes
        int test_id = repo.sendTestsDataToLab(testreq.getPatient_id(), testreq.getLab_id()); // Returns an int that will be used to associate the results with the request
        if(test_id < 0)
            return ResponseEntity.status(500).build();

        try {
            //TODO: Include the shared secret
            //URL url = new URL("https://192.168.57.11:8080/teststoanalyze/" + test_id);
            //HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //InputStream responseStream = connection.getInputStream();

            // Get InputStream and convert it to JSON
            // The custom protocol could use this to convert the data before redirecting it to the API
            //InputStream responseStream = connection.getInputStream();
            //ObjectMapper mapper = new ObjectMapper();
            //Map<String, Object> jsonMap = mapper.readValue(responseStream, Map.class);

            return ResponseEntity.ok().build();
        } catch(Exception e) {
            System.out.println("Unable to make HTTP Request");
            return ResponseEntity.status(500).build();
        }
    }

    // Receives the test results from the Lab API
    @PostMapping("/sendtestresults/{test_id}")
    public ResponseEntity<String> postTestResults(@PathVariable int test_id, @RequestBody TestResults results) {
        boolean r = repo.updateTestResults(test_id, results);

        if(r)
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(500).build();
    }

    @GetMapping("/login")
    public String login() {
        return "Hello there buddy, how are u";
    }
}
