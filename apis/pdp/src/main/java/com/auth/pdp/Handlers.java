package com.auth.pdp;

import com.auth.pdp.entities.XACMLRequest;
import com.auth.pdp.entities.XACMLResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Handlers {
    final String PERMIT_DECISION = "Permit";
    final String DENY_DECISION = "Deny";
    PDP pdp = new PDP();

    @PostMapping("/pdp")
    public ResponseEntity<XACMLResponse> validateRequest(@RequestBody XACMLRequest xreq) {
        if(pdp.checkPolicies(xreq))
            return ResponseEntity.ok(new XACMLResponse(PERMIT_DECISION));
        else
            return ResponseEntity.ok(new XACMLResponse(DENY_DECISION));
    }
}
