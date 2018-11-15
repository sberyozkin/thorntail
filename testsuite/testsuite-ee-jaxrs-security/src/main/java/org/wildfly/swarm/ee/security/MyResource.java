package org.wildfly.swarm.ee.security;

import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/security")
public class MyResource {

    @Inject
    private SecurityContext sc;

    @GET
    public String getNameAndRoles() {
        String callerName = null;
        if (sc.getCallerPrincipal() != null) {
            callerName = sc.getCallerPrincipal().getName();
        }
        return callerName + ", role1:" + sc.isCallerInRole("role1") + ", role2:" + sc.isCallerInRole("role2");
    }

}
