package com.auth.pdp.entities;

public class Rules {
    String RuleId;
    String Effect;
    Target Target;

    public String getEffect() {
        return Effect;
    }

    public String getRuleId() {
        return RuleId;
    }

    public com.auth.pdp.entities.Target getTarget() {
        return Target;
    }

    public void setEffect(String effect) {
        Effect = effect;
    }

    public void setRuleId(String ruleId) {
        RuleId = ruleId;
    }

    public void setTarget(com.auth.pdp.entities.Target target) {
        Target = target;
    }
}
