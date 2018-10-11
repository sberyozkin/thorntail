package io.thorntail.security.keycloak.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;

/**
 * Created by bob on 1/18/18.
 */
@ApplicationScoped
public class KeycloakDeploymentsProducer {

    @Inject
    @ConfigProperty(name = "keycloak.json.path", defaultValue = "keycloak.json")
    String keycloakJsonPath;

    @Inject
    @ConfigProperty(name = "keycloak.multitenancy.paths")
    Optional<Map<String, String>> keycloakMultitenancyPaths;

    @Produces
    public KeycloakConfigResolver staticResolver() {
        KeycloakDeployment dep = loadKeycloakDeployment(keycloakJsonPath);
        return dep != null ? new StaticKeycloakConfigResolver(dep) : null; 
    }
    
    @Produces
    public KeycloakConfigResolver multitenancyResolver() {
        if (keycloakMultitenancyPaths.isPresent()) {
            Map<String, KeycloakDeployment> pathDeployments = new HashMap<>();
            for (Map.Entry<String, String> entry : keycloakMultitenancyPaths.get().entrySet()) {
                KeycloakDeployment dep = loadKeycloakDeployment(entry.getKey());
                if (dep != null) {
                    pathDeployments.put(entry.getKey(), dep);
                }
            }
            return new KeycloakMultitenancyConfigResolver(pathDeployments);
        } else {
            return null;
        }
    }

    private static KeycloakDeployment loadKeycloakDeployment(String path) {
        KeycloakDeployment dep = KeycloakUtils.loadFromClasspath(path);
        if (dep == null && !path.startsWith("classpath:")) {
            dep = KeycloakUtils.loadFromFilesystem(path);
        }
        return dep;
    }
}
