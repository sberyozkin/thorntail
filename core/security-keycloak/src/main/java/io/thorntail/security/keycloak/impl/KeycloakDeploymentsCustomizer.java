package io.thorntail.security.keycloak.impl;

import static io.thorntail.Info.ROOT_PACKAGE;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.thorntail.condition.annotation.RequiredClassPresent;
import io.thorntail.events.LifecycleEvent;
import io.thorntail.servlet.DeploymentMetaData;
import io.thorntail.servlet.Deployments;
import io.thorntail.servlet.SecurityConstraintMetaData;

/**
 * Created by bob on 1/18/18.
 */
@ApplicationScoped
@RequiredClassPresent(ROOT_PACKAGE + ".servlet.Deployments")
public class KeycloakDeploymentsCustomizer {

    void customize(@Observes @Priority(100) LifecycleEvent.Initialize event) {
        this.deployments.stream().forEach(this::customize);
    }

    void customize(DeploymentMetaData deployment) {
        if (deployment.getAuthMethods().contains("KEYCLOAK")) {
            SecurityKeycloakMessages.MESSAGES
                .configResolverForDeployment(deployment.getName() == null ? "" : deployment.getName());
            deployment.addInitParam("keycloak.config.resolver", ConfigResolver.class.getName());
            deployment.setRealm("");
            
            if (keycloakSecurityConstraints.isPresent()) {
                for (Map.Entry<String, String> entry : keycloakSecurityConstraints.get().entrySet()) {
                    SecurityConstraintMetaData sc = new SecurityConstraintMetaData();
                    
                }
            }
            
        } else {
            SecurityKeycloakMessages.MESSAGES
                .noKeycloakForDeployment(deployment.getName() == null ? "" : deployment.getName());
        }
    }

    @Inject
    Deployments deployments;
    
    @Inject
    @ConfigProperty(name = "keycloak.security-constraints")
    Optional<Map<String, String>> keycloakSecurityConstraints;

}
