package io.tavisco.rvstore.cars;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.tavisco.rvstore.cars.TokenUtils.generateValidPrimaryUserToken;
import static org.hamcrest.CoreMatchers.is;

@Tag("integration")
@QuarkusTest
class DebugResourceTest {

    @Test
    void testAuthEndpointWithoutAuthorization() {
        given()
            .when()
                .get("/api/debug/auth")
            .then()
                .statusCode(200)
                .body(is("hello + anonymous, isSecure: false, authScheme: null, uid: null, nickname: null groups: "));
    }

    @Test
    void testAuthEndpointWithAuthorization() {
        given()
            .auth().oauth2(generateValidPrimaryUserToken())
            .when()
                .get("/api/debug/auth")
            .then()
                .statusCode(200)
                .body(is("hello + primary@quarkus.io, isSecure: false, authScheme: Bearer, uid: 123UID, nickname: Nickname groups: [Everyone]"));
    }

}