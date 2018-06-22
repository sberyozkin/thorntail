package io.thorntail.testsuite.security.keycloak;

import static io.restassured.RestAssured.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.thorntail.test.ThorntailTestRunner;

@RunWith(ThorntailTestRunner.class)
public class SecurityKeycloakAppIT {

    @Test
    public void test() throws Exception {
         when()
            .get("/secured")
            .then()
            .statusCode(500);

    }
}
