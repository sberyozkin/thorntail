package io.thorntail.testsuite.security.keycloak;

import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.thorntail.test.ThorntailTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.when;

@RunWith(ThorntailTestRunner.class)
public class SecurityKeycloakAppIT {

    @Test
    public void test() throws Exception {
         when()
            .get("/secured")
            .then()
            .statusCode(401);

    }
}
