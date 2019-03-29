/**
 * Copyright 2015-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.swarm.keycloak.saml.runtime;

import static org.wildfly.swarm.spi.api.Defaultable.string;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.wildfly.swarm.config.runtime.AttributeDocumentation;
import org.wildfly.swarm.spi.api.Defaultable;
import org.wildfly.swarm.spi.api.DeploymentProcessor;
import org.wildfly.swarm.spi.api.annotations.Configurable;
import org.wildfly.swarm.spi.runtime.annotations.DeploymentScoped;

@DeploymentScoped
public class KeycloakSamlArchivePreparer implements DeploymentProcessor {

    private static final Logger LOG = Logger.getLogger(KeycloakSamlArchivePreparer.class);

    private final Archive<?> archive;

    @Inject
    public KeycloakSamlArchivePreparer(Archive archive) {
        this.archive = archive;
    }

    @Override
    public void process() throws IOException {
        addResourceToArchive(keycloakSamlXmlPath.get());
        addResourceToArchive(keycloakSamlKeystorePath.get());
    }

    private void addResourceToArchive(String resourceName) {
        InputStream resourceStream = getResourceFromClasspath(resourceName);
        if (resourceStream != null) {
            archive.add(new ByteArrayAsset(resourceStream), "WEB-INF/" + resourceName);
        } else {
            LOG.warn(String.format("Unable to get the resource '%s'", resourceName));
        }
    }

    private InputStream getResourceFromClasspath(String resourceName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream(resourceName);
        if (is == null) {

            Node node = archive.get(resourceName);
            if (node == null) {
                node = getNodeFromWebInf(resourceName, true);
            }
            if (node == null) {
                node = getNodeFromWebInf(resourceName, false);
            }
            if (node != null && node.getAsset() != null) {
                is = node.getAsset().openStream();
            }
        }
        return is;
    }

    private Node getNodeFromWebInf(String resourceName,
            boolean useForwardSlash) {
        String webInfPath = useForwardSlash ? "/WEB-INF" : "WEB-INF";
        if (!resourceName.startsWith("/")) {
            resourceName = "/" + resourceName;
        }
        Node xmlNode = archive.get(webInfPath + resourceName);
        if (xmlNode == null) {
            xmlNode = archive.get(webInfPath + "/classes" + resourceName);
        }
        return xmlNode;
    }

    @AttributeDocumentation("Path to Keycloak SAML adapter configuration")
    @Configurable("thorntail.keycloak.saml.xml.path")
    private Defaultable<String> keycloakSamlXmlPath = string("keycloak-saml.xml");

    @AttributeDocumentation("Path to Keycloak SAML adapter keystore")
    @Configurable("thorntail.keycloak.saml.keystore.path")
    private Defaultable<String> keycloakSamlKeystorePath = string("keystore.jks");
}
