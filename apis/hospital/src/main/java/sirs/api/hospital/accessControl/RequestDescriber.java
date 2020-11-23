package sirs.api.hospital.accessControl;

public class RequestDescriber {
    private String resourceId;      // e.g. getPatientDiseases
    private String method;          // e.g. GET, POST...
    private String reqUri;          // e.g. "/getPatientDiseases/1"
    private String role = "Unauthenticated";            // e.g. doctor, nurse...

    public RequestDescriber(String resourceId, String method, String reqUri) {
        this.resourceId = resourceId;
        this.method = method;
        this.reqUri = reqUri;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getReqUri() {
        return reqUri;
    }

    public void setReqUri(String reqUri) {
        this.reqUri = reqUri;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
