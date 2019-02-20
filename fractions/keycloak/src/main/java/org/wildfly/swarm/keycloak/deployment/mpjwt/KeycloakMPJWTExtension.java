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

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;


/**
 * A Keycloak CDI extension that provides a producer for the current authenticated JsonWebToken based on
 * a thread local value that is managed by the {@link JWTAuthMechanism} request authentication handler.
 *
 */
public class KeycloakMPJWTExtension implements Extension {

    /**
     * Register the MPJWTProducer JsonWebToken producer bean
     *
     * @param bbd         before discovery event
     * @param beanManager cdi bean manager
     */
    public void observeBeforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd, BeanManager beanManager) {
        String extensionName = KeycloakMPJWTExtension.class.getName();
        bbd.addAnnotatedType(beanManager.createAnnotatedType(KeycloakJWTAuthContextInfoProvider.class),
            extensionName  + "_" + KeycloakJWTAuthContextInfoProvider.class.getName());
    }
}
