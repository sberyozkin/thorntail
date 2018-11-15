package org.wildfly.swarm.ee.security;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@DeclareRoles({ "role1", "role2" })
@ApplicationPath("/")
public class MyApplication extends Application {
}
