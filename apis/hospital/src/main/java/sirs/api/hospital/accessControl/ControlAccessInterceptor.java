package sirs.api.hospital.accessControl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import sirs.api.hospital.db.Repo;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ControlAccessInterceptor implements HandlerInterceptor {
    final private String pdpUrl = "http://127.0.0.1:8081/pdp";//"http://192.168.56.12/pdp";
    final private String authenticationHeader = "Authorization";
    Repo db = new Repo();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Build a request describer
        RequestDescriber req = buildRequestDescriber(request.getRequestURI(), request.getMethod(), handler);

        // Asked for nonexistent resource, small optimization to avoid a couple trips when the answer is known
        if(req.getResourceId().equals("")) {
            System.out.println("[AC Interceptor] Terminating request, requested resource does not exist.");
            return false;
        }

        // If the resource trying to access is not the login endpoint it has to do extra work
        // like checking if the authentication token is valid and if so associate that information
        // with the request.
        if(!req.getResourceId().equals("login")) {
            // Get access token from the 'Authorization' header.
            String accessToken = request.getHeader(authenticationHeader);

            // Not authorized to access any resource if subject is not authenticated & wants to access a resource
            // different from the login endpoint.
            if(accessToken == null) {
                System.out.println("[AC Interceptor] Terminating request, no access token provided.");
                return false;
            }

            // Validates the authenticity of the token, if role is empty then there is no session
            // with this token.
            String role = validateToken(accessToken);
            if(role.equals("")) {
                System.out.println("[AC Interceptor] Terminating request, the provided token is not associated to any session.");
                return false;
            }

            req.setRole(role);
        }

        System.out.println("[AC Interceptor] Asking PDP decision on: ["+req.getMethod()+"] ["+req.getRole()+"] ["+req.getResourceId()+"]");
        // Builds the XACMLRequest, forwards it to the PDP and if authorized lets request continue
//        XACMLRequest xreq = buildxacmlreq(req);
//        return checkAuthorization(xreq);
        return true;
    }

    /**
     *  Forwards a XACMLRequest to the "Policy Decision Point" and awaits an answer
     *  if the request is allowed to continue it will return true.
     */
    private Boolean checkAuthorization(XACMLRequest xreq) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Write body
            String reqBody = mapper.writeValueAsString(xreq);

            // Send POST request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pdpUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();

            // Read response and convert
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            XACMLResponse resp = mapper.readValue(response.body(), XACMLResponse.class);

            if(resp.Response[0].getDecision().equals("Permit")) {
                System.out.println("[AC Interceptor] PDP answered with PERMIT code, request is allowed to continue.");
                return true;
            } else {
                System.out.println("[AC Interceptor] PDP answered with DENY code, request is not allowed to continue.");
                return false;
            }
        } catch(Exception e) {
            System.out.println("I guess no HTTP requests for you :(");
            return false;
        }
    }

    /**
     *  Build's a request with the format specified by the XACML Framework.
     */
    private XACMLRequest buildxacmlreq(RequestDescriber req) {
        return new XACMLRequest(req);
    }

    // Check if the token is valid and returns the associated role
    private String validateToken(String accessToken) {
        return db.validateToken(accessToken);
    }

    /**
     * Builds an object that describes the request (method, resource...)
     * this info will be used to build the XACML Request.
     *
     * The most important info in this method is the "ResourceId" which associates
     * a resource with a identification which will be used to check the policies.
     */
    private RequestDescriber buildRequestDescriber(String reqUri, String method, Object handler) {
        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method mh = handlerMethod.getMethod();
            ResourceId resourceIdentifierAnnotation = mh.getAnnotation(ResourceId.class);
            String resourceName = resourceIdentifierAnnotation.resourceId();

            return new RequestDescriber(resourceName, method, reqUri);
        } catch(Exception e) {
            System.out.println("Method has no annotation identifying the resource...");
            return new RequestDescriber("", method, reqUri);
        }
    }
}
