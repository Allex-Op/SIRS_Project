package com.auth.pdp.entities;

public class XACMLResponse {
    public Response[] Response;

    public XACMLResponse(String decision) {
        this.Response = new Response[] {new Response(decision)};
    }

    public com.auth.pdp.entities.Response[] getResponse() {
        return Response;
    }

    public void setResponse(com.auth.pdp.entities.Response[] response) {
        Response = response;
    }
}

class Response {
    String Decision;

    public Response(String decision) {
        this.Decision = decision;
    }

    public String getDecision() {
        return Decision;
    }

    public void setDecision(String decision) {
        Decision = decision;
    }
}
