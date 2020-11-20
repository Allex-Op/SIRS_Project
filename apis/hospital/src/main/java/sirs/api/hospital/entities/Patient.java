package sirs.api.hospital.entities;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Patient {
    public String name;
    public String diseases;
    public String treatment;
    public LinkedList<String> results;

    public void setResults(LinkedList<String> results) {
        this.results = results;
    }

    public LinkedList<String> getResults() {
        return results;
    }

    public String getDiseases() {
        return diseases;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getName() {
        return name;
    }

    public void setDiseases(String diseases) {
        this.diseases = diseases;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public void setName(String name) {
        this.name = name;
    }
}
