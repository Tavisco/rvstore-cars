package io.tavisco.rvstore.cars;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;

import io.tavisco.rvstore.cars.dto.CarAuthorDto;
import io.tavisco.rvstore.cars.dto.CarDto;
import org.eclipse.microprofile.jwt.Claims;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import java.util.*;

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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CarResourceTest {

    private static final Long UNKNOWN_CAR_ID = 999L;
    private static final String DEFAULT_NAME = "Test car";
    private static final String DEFAULT_DESCRIPTION = "Car created to test the microsservice";
    private static final String DEFAULT_AUTHOR1_NAME = "Default author one";
    private static final String DEFAULT_AUTHOR2_NAME = "Default author two";
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
            .header(ACCEPT, APPLICATION_JSON)
            .pathParam("id", UNKNOWN_CAR_ID)
        .when()
            .get("/api/cars/{id}")
        .then()
            .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void shouldNotAddCarWhenUserIsNotAuthenticated() {
        CarDto car = CarDto.builder()
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
        List<CarAuthorDto> authors = new ArrayList<>();
        authors.add(CarAuthorDto.builder()
                                    .name(DEFAULT_AUTHOR1_NAME)
                                    .build());

        authors.add(CarAuthorDto.builder()
                                    .name(DEFAULT_AUTHOR2_NAME)
                                    .build());

        CarDto invalidCar = CarDto.builder()
                                    .name(null)
                                    .description(DEFAULT_DESCRIPTION)
                                    .authors(authors)
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
    void shouldNotAddCarWithoutAuthors() {
        CarDto invalidCar = CarDto.builder()
                                    .name(DEFAULT_NAME)
                                    .description(DEFAULT_DESCRIPTION)
                                    .authors(null)
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
        List<CarAuthorDto> authors = new ArrayList<>();
        authors.add(CarAuthorDto.builder()
                                    .name(DEFAULT_AUTHOR1_NAME)
                                    .build());

        authors.add(CarAuthorDto.builder()
                                    .name(DEFAULT_AUTHOR2_NAME)
                                    .build());

        CarDto car = CarDto.builder()
                                .name(DEFAULT_NAME)
                                .description(DEFAULT_DESCRIPTION)
                                .authors(authors)
                                .build();

        String location = given()
                            .auth().oauth2(generateValidUserToken())
                            .body(car)
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
        assertNotNull(carId);

        given()
            .pathParam("id", carId)
            .header(ACCEPT, APPLICATION_JSON)
        .when()
            .get("/api/cars/{id}")
        .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .body("name", is(DEFAULT_NAME))
            .body("description", is(DEFAULT_DESCRIPTION))
            .body("authors.size()", is(2))
            .body("authors[0].name", is(DEFAULT_AUTHOR1_NAME))
            .body("authors[1].name", is(DEFAULT_AUTHOR2_NAME));

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