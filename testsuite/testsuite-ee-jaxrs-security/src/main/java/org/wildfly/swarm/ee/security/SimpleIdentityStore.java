package org.wildfly.swarm.ee.security;

import static java.util.Arrays.asList;
import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;

import java.util.HashSet;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

@ApplicationScoped
public class SimpleIdentityStore implements IdentityStore {

    public CredentialValidationResult validate(UsernamePasswordCredential usernamePasswordCredential) {

        if (usernamePasswordCredential.compareTo("thorntail1", "secret1")) {
            return new CredentialValidationResult("thorntail1", new HashSet<>(asList("role1")));
        } else if (usernamePasswordCredential.compareTo("thorntail2", "secret2")) {
            return new CredentialValidationResult("thorntail2", new HashSet<>(asList("role2")));
        }

        return INVALID_RESULT;
    }

}

