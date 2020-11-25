package com.auth.pdp.entities;

public class Target {
    Subjects[] Subjects;
    Resources[] Resources;
    Actions[] Actions;

    public com.auth.pdp.entities.Actions[] getActions() {
        return Actions;
    }

    public com.auth.pdp.entities.Resources[] getResources() {
        return Resources;
    }

    public com.auth.pdp.entities.Subjects[] getSubjects() {
        return Subjects;
    }

    public void setActions(com.auth.pdp.entities.Actions[] actions) {
        Actions = actions;
    }

    public void setResources(com.auth.pdp.entities.Resources[] resources) {
        Resources = resources;
    }

    public void setSubjects(com.auth.pdp.entities.Subjects[] subjects) {
        Subjects = subjects;
    }
}
