package sirs.api.hospital.accessControl;

import sirs.api.hospital.OperationMode;

public class XACMLRequest {
    Request Request;

    public XACMLRequest(RequestDescriber req) {
        this.Request = new Request(req.getResourceId(), req.getRole(), req.getMethod());
    }

    public sirs.api.hospital.accessControl.Request getRequest() {
        return Request;
    }

    public void setRequest(sirs.api.hospital.accessControl.Request request) {
        Request = request;
    }
}

class Request {
    AccessSubject AccessSubject;
    Action Action;
    Resource Resource;
    Environment Environment;

    public Request(String resourceId, String subjectRole, String method) {
        this.AccessSubject = new AccessSubject(subjectRole);
        this.Action = new Action(method);
        this.Resource = new Resource(resourceId);
        this.Environment = new Environment();
    }

    public sirs.api.hospital.accessControl.AccessSubject getAccessSubject() {
        return AccessSubject;
    }

    public sirs.api.hospital.accessControl.Action getAction() {
        return Action;
    }

    public sirs.api.hospital.accessControl.Environment getEnvironment() {
        return Environment;
    }

    public sirs.api.hospital.accessControl.Resource getResource() {
        return Resource;
    }
}

class AccessSubject {
    public AttributeId[] Attribute;

    public AccessSubject(String subjectRole) {
        this.Attribute = new AttributeId[] {
                new AttributeId("subject-role", subjectRole)
        };
    }

    public AttributeId[] getAttribute() {
        return Attribute;
    }

    public void setAttribute(AttributeId[] attribute) {
        Attribute = attribute;
    }
}

class Action {
    public AttributeId[] Attribute;

    public Action(String method) {
        this.Attribute = new AttributeId[] {
                new AttributeId("action-id", method)
        };
    }

    public AttributeId[] getAttribute() {
        return Attribute;
    }

    public void setAttribute(AttributeId[] attribute) {
        Attribute = attribute;
    }
}

class Resource {
    public AttributeId[] Attribute;

    public Resource(String resourceId) {
        this.Attribute = new AttributeId[] {
                new AttributeId("resource-id", resourceId)
        };
    }

    public AttributeId[] getAttribute() {
        return Attribute;
    }

    public void setAttribute(AttributeId[] attribute) {
        Attribute = attribute;
    }
}

class Environment {
    public AttributeId[] Attribute;

    public Environment() {
        this.Attribute = new AttributeId[] { new AttributeId("Context", OperationMode.getPandemicMode()) };
    }

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

    public AttributeId(String attributeId, String value) {
        this.AttributeId = attributeId;
        this.Value = value;
    }

    public String getAttributeId() {
        return AttributeId;
    }

    public String getValue() {
        return Value;
    }

    public void setAttributeId(String attributeId) {
        AttributeId = attributeId;
    }

    public void setValue(String value) {
        Value = value;
    }
}