package io.tavisco.rvstore.cars;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;

import org.eclipse.microprofile.jwt.Claims;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.build.Jwt;
import io.tavisco.rvstore.cars.models.Car;

@QuarkusTest
@QuarkusTestResource(DatabaseResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CarResourceTest {

    private static final Long UNKNOWN_CAR_ID = 999L;
    private static final String DEFAULT_NAME = "Test car";
    private static final String DEFAULT_DESCRIPTION = "Car created to test the microsservice";

    @Test
    public void testHelloEndpoint() {
        given()
            .when().get("/api/cars/hello")
            .then()
            .statusCode(200)
            .body(is("hello"));
    }

    @Test
    void shouldNotGetUnknownCar() {
        given()
            .pathParam("id", UNKNOWN_CAR_ID)
            .when().get("/api/cars/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void shouldNotAddCarWhenUserIsNotAuthenticated() {
        Car car = Car.builder()
                        .name(DEFAULT_NAME)
                        .description(DEFAULT_DESCRIPTION)
                        .build();

        given()
            .body(car)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/cars")
            .then()
            .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void shouldNotAddInvalidCar() {
        Car invalidCar = Car.builder()
                                .name(null)
                                .description(DEFAULT_DESCRIPTION)
                                .build();

        given()
            .auth().oauth2(generateValidUserToken())
            .body(invalidCar)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/cars")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());
    }

    private static String generateValidUserToken() {
        return Jwt.upn("jdoe@quarkus.io")
        		   .issuer("https://quarkus.io/using-jwt-rbac")
        		   .groups("Everyone")
        		   .claim(Claims.birthdate.name(), "2001-07-13")
        		   .sign();
    }
    
}