package io.thorntail.testsuite.security.keycloak;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/secured")
public class SecuredResource {

    @GET
    public String get() {
        return "Hi, this resource is secured";
    }

}
