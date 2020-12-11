package com.auth.pdp;

import com.auth.pdp.entities.*;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;

public class PDP {
    final String PROJECT_PATH = System.getenv("PROJECT_PATH");

    public boolean checkPolicies(XACMLRequest xreq) {
        PolicySet pc = readPolicies();
        if(pc == null)
            return false;

        return verifyPolicies(xreq, pc);
    }

    private boolean verifyPolicies(XACMLRequest xreq, PolicySet pc) {
        Request req = xreq.getRequest();
        String role = req.getAccessSubject().getAttribute()[0].Value;           //e.g. Doctor
        String method = req.getAction().getAttribute()[0].Value;                //e.g. POST
        String resourceId = req.getResource().getAttribute()[0].Value;          //e.g. Login
        String environment = req.getEnvironment().getAttribute()[0].Value;      //e.g. normal (or pandemic)

        Rules[] rules;
        if(environment.equals("normal"))
            rules = pc.getPolicies()[0].getRules();
        else
            rules = pc.getPolicies()[1].getRules();

        return matchRules(role, method, resourceId, rules);
    }

    private boolean matchRules(String role, String method, String resourceId, Rules[] rules) {
        for(Rules rule: rules ) {
            //  Last rule, default behavior when no rule matched
            if(rule.getRuleId().equals("Default"))
                return !rule.getEffect().equals("Deny");

            Target target = rule.getTarget();

            //If the rule is applied to the requested resource, e.g. requested login resource
            //only proceed if the rule applies to the login resource, otherwise keep searching
            //for the adequate rule.
            if(target.getResources()[0].getValue().equals(resourceId)) {
                return verifyRule(role.toLowerCase(), method, target);
            }
        }
        //Unreachable if policies and verification are well implemented
        return false;
    }

    private boolean verifyRule(String role, String method, Target target) {
        for(Subjects subject: target.getSubjects()) {
            String subjectRole = subject.getValue().toLowerCase();
            if(subjectRole.equals(role) || subjectRole.equals("anyone")) {
                // Verify the method, if the method in the rule matches the requested method
                // allow request to continue, otherwise reject request.
                return target.getActions()[0].getValue().equals(method);
            }
        }

        // User tried to access a resource which his role has no authorization, e.g. Janitor to "/testresults"
        return false;
    }

    private PolicySet readPolicies() {
        try {
            File policiesFile = new File(PROJECT_PATH + "policies.json");
            FileInputStream fis = new FileInputStream(policiesFile);
            byte[] data = new byte[(int) policiesFile.length()];
            fis.read(data);
            fis.close();

            String policies = new String(data, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            return mapper.readValue(policies, PolicySet.class);
        } catch(Exception e) {
            System.out.println("Error reading policies");
            return null;
        }
    }
}
