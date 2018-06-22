package io.thorntail.testsuite.security.keycloak;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.auth.LoginConfig;

import io.thorntail.Thorntail;

@ApplicationPath("/")
@LoginConfig(authMethod = "KEYCLOAK")
public class SecuredApplication extends Application {
    public static void main(String... args) throws Exception {
        Thorntail.run();
    }
}
