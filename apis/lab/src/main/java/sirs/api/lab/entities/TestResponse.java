package sirs.api.lab.entities;

public class TestResponse {
    String results;
    String digitalSignature;
//    String freshness;

    public TestResponse(String results, String digitalSignature) {
        this.results = results;
        this.digitalSignature = digitalSignature;
//        this.freshness = freshness;
    }

    public String getResults() {
        return results;
    }

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public void setResults(String results) {
        this.results = results;
    }

//    public String getFreshness() { return freshness; }
//
//    public void setFreshness(String freshness) { this.freshness = freshness; }
}
