package com.auth.pdp.entities;

public class Resource {
    public AttributeId[] Attribute;

    public AttributeId[] getAttribute() {
        return Attribute;
    }

    public void setAttribute(AttributeId[] attribute) {
        Attribute = attribute;
    }
}
