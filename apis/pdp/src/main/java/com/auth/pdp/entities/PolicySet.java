package com.auth.pdp.entities;


public class PolicySet {
    String PolicySetId;
    Policies[] Policies;

    public String getPolicysetid() {
        return PolicySetId;
    }

    public void setPolicysetid(String policysetid) {
        this.PolicySetId = policysetid;
    }

    public com.auth.pdp.entities.Policies[] getPolicies() {
        return Policies;
    }

    public void setPolicies(com.auth.pdp.entities.Policies[] policies) {
        this.Policies = policies;
    }
}

