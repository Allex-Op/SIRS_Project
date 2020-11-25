package com.auth.pdp.entities;

public class Request {
    AccessSubject AccessSubject;
    Action Action;
    Resource Resource;
    Environment Environment;

    public com.auth.pdp.entities.Resource getResource() {
        return Resource;
    }

    public com.auth.pdp.entities.Environment getEnvironment() {
        return Environment;
    }

    public com.auth.pdp.entities.Action getAction() {
        return Action;
    }

    public com.auth.pdp.entities.AccessSubject getAccessSubject() {
        return AccessSubject;
    }

    public void setAccessSubject(com.auth.pdp.entities.AccessSubject accessSubject) {
        AccessSubject = accessSubject;
    }

    public void setAction(com.auth.pdp.entities.Action action) {
        Action = action;
    }

    public void setEnvironment(com.auth.pdp.entities.Environment environment) {
        Environment = environment;
    }

    public void setResource(com.auth.pdp.entities.Resource resource) {
        Resource = resource;
    }
}
