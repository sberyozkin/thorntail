package org.wildfly.swarm.keycloak;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

@Path("employee-sig")
public class SecuredResource {

    @GET
    public String get(@Context SecurityContext sc) {
        return "Hi " + sc.getUserPrincipal().getName() + ", this resource is secured";
    }

}
