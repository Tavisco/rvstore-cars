package io.tavisco.rvstore.cars;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;

import org.eclipse.microprofile.jwt.Claims;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import io.smallrye.jwt.build.Jwt;
import io.tavisco.rvstore.cars.models.Car;
import io.tavisco.rvstore.cars.models.CarAuthor;

@QuarkusTest
@QuarkusTestResource(DatabaseResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CarResourceTest {

    private static final Long UNKNOWN_CAR_ID = 999L;
    private static final String DEFAULT_NAME = "Test car";
    private static final String DEFAULT_DESCRIPTION = "Car created to test the microsservice";
    private static final String DEFAULT_AUTHOR_NAME = "Default author";
    private static final Integer INITIAL_CARS_QTY = 0;

    private static String carId;

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

    @Test
    @Order(1)
    void shouldNotHaveInitialCars() {
        List<Car> cars = get("/api/cars").then()
                            .statusCode(OK.getStatusCode())
                            .header(CONTENT_TYPE, APPLICATION_JSON)
                            .extract().body().as(getCarTypeRef());

        assertEquals(INITIAL_CARS_QTY, cars.size());
    }

    @Test
    @Order(2)
    void shouldAddCar() {

        // Car car = Car.builder()
        //                 .name(DEFAULT_NAME)
        //                 .description(DEFAULT_DESCRIPTION)
        //                 .build();

        // car.authors = new HashSet<CarAuthor>(){{
        //     add(CarAuthor.builder().name(DEFAULT_AUTHOR_NAME).car(car).build());
        //   }};git

        String location = given()
                            .auth().oauth2(generateValidUserToken())
                            .body("{\"name\": \"Test car\", \"description\": \"Car created to test the microsservice\", \"authors\": {\"name\": \"Default author\"}}")
                            .header(CONTENT_TYPE, APPLICATION_JSON)
                            .header(ACCEPT, APPLICATION_JSON)
                            .when()
                            .post("/api/cars")
                            .then()
                            .statusCode(CREATED.getStatusCode())
                            .extract().header("Location");

        assertTrue(location.contains("/api/cars"));

        // Stores the id for future CRUD tests
        String[] segments = location.split("/");
        carId = segments[segments.length - 1];
        assertNotNull(carId);
    }

    @Test
    @Order(3)
    void shouldRetrieveCreatedCar() {
        given()
            .pathParam("id", carId)
            .when().get("/api/cars/{id}")
            .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .body("name", Is.is(DEFAULT_NAME))
            .body("description", Is.is(DEFAULT_DESCRIPTION))
            .body("authors[0].name", Is.is(DEFAULT_AUTHOR_NAME));

        List<Car> cars = get("/api/cars").then()
                                            .statusCode(OK.getStatusCode())
                                            .header(CONTENT_TYPE, APPLICATION_JSON)
                                            .extract().body().as(getCarTypeRef());

        assertEquals(INITIAL_CARS_QTY + 1, cars.size());
    }

    private TypeRef<List<Car>> getCarTypeRef() {
        return new TypeRef<List<Car>>() {
            // Kept empty on purpose
        };
    }

    private static String generateValidUserToken() {
        return Jwt.upn("jdoe@quarkus.io")
        		   .issuer("https://quarkus.io/using-jwt-rbac")
        		   .groups("Everyone")
        		   .claim(Claims.birthdate.name(), "2001-07-13")
        		   .sign();
    }
    
}