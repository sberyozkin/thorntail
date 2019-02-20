/**
 *
 *   Copyright 2017 Red Hat, Inc, and individual contributors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.wildfly.swarm.keycloak.deployment.mpjwt;

import java.util.Optional;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;

/**
 * CDI provider for the JWTAuthContextInfo
 */
@Dependent
@Priority(100)
@Alternative
public class KeycloakJWTAuthContextInfoProvider {
    @Inject
    @ConfigProperty(name = "keycloak.json.location", defaultValue = "keycloak.json")
    private String issuedBy;

    /**
     * Produce the JWTAuthContextInfo.
     * @return an Optional wrapper for the configured JWTAuthContextInfo
     */
    @Produces
    public Optional<JWTAuthContextInfo> getOptionalContextInfo() {
        JWTAuthContextInfo contextInfo = new JWTAuthContextInfo();
        return Optional.of(contextInfo);
    }

    @Produces
    public JWTAuthContextInfo getJWTAuthContextInfo() {
        return getOptionalContextInfo().get();
    }
}
