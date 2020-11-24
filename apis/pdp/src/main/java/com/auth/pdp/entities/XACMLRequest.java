package com.auth.pdp.entities;

public class XACMLRequest {
    Request Request;

    public com.auth.pdp.entities.Request getRequest() {
        return Request;
    }

    public void setRequest(com.auth.pdp.entities.Request request) {
        Request = request;
    }
}

class Request {
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

class AccessSubject {
    public AttributeId[] Attribute;

    public AttributeId[] getAttribute() {
        return Attribute;
    }

    public void setAttribute(AttributeId[] attribute) {
        Attribute = attribute;
    }
}

class Action {
    public AttributeId[] Attribute;

    public AttributeId[] getAttribute() {
        return Attribute;
    }

    public void setAttribute(AttributeId[] attribute) {
        Attribute = attribute;
    }
}

class Resource {
    public AttributeId[] Attribute;

    public AttributeId[] getAttribute() {
        return Attribute;
    }

    public void setAttribute(AttributeId[] attribute) {
        Attribute = attribute;
    }
}

class Environment {
    public AttributeId[] Attribute;

    public AttributeId[] getAttribute() {
        return Attribute;
    }

    public void setAttribute(AttributeId[] attribute) {
        Attribute = attribute;
    }
}

class AttributeId {
    public String AttributeId;
    public String Value;

    public String getValue() {
        return Value;
    }

    public String getAttributeId() {
        return AttributeId;
    }

    public void setValue(String value) {
        Value = value;
    }

    public void setAttributeId(String attributeId) {
        AttributeId = attributeId;
    }
}