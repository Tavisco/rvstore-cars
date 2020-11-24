package io.tavisco.rvstore.cars;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.tavisco.rvstore.cars.TokenUtils.generateValidUserToken;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class DebugResourceTest {

    @Test
    void testAuthEndpointWithoutAuthorization() {
        given()
            .when()
                .get("/api/debug/auth")
            .then()
                .statusCode(200)
                .body(is("hello + anonymous, isSecure: false, authScheme: null, hasJWT: true, uid: null, nickname: null groups: "));
    }

    @Test
    void testAuthEndpointWithAuthorization() {
        given()
            .auth().oauth2(generateValidUserToken())
            .when()
                .get("/api/debug/auth")
            .then()
                .statusCode(200)
                .body(is("hello + jdoe@quarkus.io, isSecure: false, authScheme: Bearer, hasJWT: true, uid: 123UID, nickname: Nickname groups: [Everyone]"));
    }

}