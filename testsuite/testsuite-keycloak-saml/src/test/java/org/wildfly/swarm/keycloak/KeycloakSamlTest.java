/**
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
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
package org.wildfly.swarm.keycloak;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.jaxrs.JAXRSArchive;


@RunWith(Arquillian.class)
public class KeycloakSamlTest {

    @Deployment
    public static Archive<?> createDeployment() throws Exception {
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "test.war");
        deployment.addClass(SecuredApplication.class);
        deployment.addClass(SecuredResource.class);
        deployment.addAsResource("project-defaults.yml");
        deployment.addAsResource("samldemo.json");
        deployment.addAsResource("keycloak-saml.xml");
        deployment.addAsResource("keystore.jks");
        return deployment;
    }

    @Test
    @RunAsClient
    public void testResourceIsSecured() throws Exception {
        Client c = ClientBuilder.newClient();
        try {
            // GET on the service endpoint, redirect to KC server is expected
            Response redirectToKcResponse = c.target("http://localhost:8080/employee-sig").request().get();
            Assert.assertEquals(302, redirectToKcResponse.getStatus());
            URI kcServerSamlUri = redirectToKcResponse.getLocation();
            
            // GET on the KC server, the authentication challenge is expected
            Invocation.Builder kcRequestBuilder = createBuilderWithCookies(c, redirectToKcResponse, kcServerSamlUri.toString());
            Response authChallengeResponse = kcRequestBuilder.get();
            String actionUri = getActionUriFromAuthForm(authChallengeResponse);
            
            // POST to the KC server with the authentication details, redirect to the response assertion SP SAML endpoint is expected
            Invocation.Builder authBuilder = createBuilderWithCookies(c, authChallengeResponse, actionUri);
            Response redirectToSamlSpResponse = authBuilder.post(Entity.form(
                        new Form().param("username", "bill").param("password", "password")));
            Assert.assertEquals(302, redirectToSamlSpResponse.getStatus());
            URI spSamlEndpointUri = redirectToSamlSpResponse.getLocation();
            
            // GET on the SP SAML (request assertion service) endpoint, redirect to the service endpoint is expected
            Invocation.Builder spSamlBuilder = createBuilderWithCookies(c, redirectToSamlSpResponse, spSamlEndpointUri.toString());
            Response redirectToServiceEndpoint = spSamlBuilder.get();
            Assert.assertEquals(302, redirectToServiceEndpoint.getStatus());
            URI serviceEndpointUri = redirectToServiceEndpoint.getLocation();
            
            // Finally get the service data
            Invocation.Builder serviceRequestBuilder = createBuilderWithCookies(c, redirectToServiceEndpoint, serviceEndpointUri.toString());
            Assert.assertEquals("Hi bill, this resource is secured", serviceRequestBuilder.get(String.class));
        } finally {
            c.close();
        }
    }

    private String getActionUriFromAuthForm(Response r) {
        String authForm = r.readEntity(String.class);
        int actionIndexStart = authForm.indexOf("action=\"") + "action=\"".length();
        int actionIndexEnd = authForm.indexOf('"', actionIndexStart);
        return authForm.substring(actionIndexStart, actionIndexEnd).replaceAll("&amp;", "&");
    }

    private Invocation.Builder createBuilderWithCookies(Client c, Response r, String uri) {
        Invocation.Builder builder = c.target(uri).request();
        for (Map.Entry<String, NewCookie> entry : r.getCookies().entrySet()) {
            builder.cookie(entry.getValue().toCookie());
        }
        return builder;
    }

}
