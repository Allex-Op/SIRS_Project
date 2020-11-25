package com.auth.pdp.entities;

public class AccessSubject {
    public AttributeId[] Attribute;

    public AttributeId[] getAttribute() {
        return Attribute;
    }

    public void setAttribute(AttributeId[] attribute) {
        Attribute = attribute;
    }
}
