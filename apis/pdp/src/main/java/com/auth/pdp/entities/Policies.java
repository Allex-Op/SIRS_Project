package com.auth.pdp.entities;

public class Policies {
    String PolicyId;
    Rules[] Rules;

    public com.auth.pdp.entities.Rules[] getRules() {
        return Rules;
    }

    public void setPolicyId(String policyId) {
        PolicyId = policyId;
    }

    public String getPolicyId() {
        return PolicyId;
    }

    public void setRules(com.auth.pdp.entities.Rules[] rules) {
        Rules = rules;
    }
}
