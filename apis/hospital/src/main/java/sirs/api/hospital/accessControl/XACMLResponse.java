package sirs.api.hospital.accessControl;

public class XACMLResponse {
    public Response[] Response;

    public sirs.api.hospital.accessControl.Response[] getResponse() {
        return Response;
    }

    public void setResponse(sirs.api.hospital.accessControl.Response[] response) {
        Response = response;
    }
}

class Response {
    String Decision;

    public String getDecision() {
        return Decision;
    }

    public void setDecision(String decision) {
        Decision = decision;
    }
}
