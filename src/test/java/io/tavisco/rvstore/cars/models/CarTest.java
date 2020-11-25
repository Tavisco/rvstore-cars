package io.tavisco.rvstore.cars.models;

import io.tavisco.rvstore.cars.dto.CarAuthorDto;
import io.tavisco.rvstore.cars.dto.CarDto;
import io.tavisco.rvstore.cars.enums.CarStep;
import io.tavisco.rvstore.cars.enums.JwtCustomClaims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    private static final String DEFAULT_NAME = "Test car";
    private static final String DEFAULT_DESCRIPTION = "Car created to test the microsservice";
    private static final String MOCK_UID = "123UID";
    private static final String MOCK_NICKNAME = "Nickname";

    @Test
    void testContructor() {
        CarDto carDto = CarDto.builder()
                .name(DEFAULT_NAME)
                .description(DEFAULT_DESCRIPTION)
                .authors(new ArrayList<>())
                .step(CarStep.DRAFT)
                .build();

        JsonWebToken jwt = mock(JsonWebToken.class);

        when(jwt.getClaim(JwtCustomClaims.UID.getText())).thenReturn(MOCK_UID);
        when(jwt.getClaim(JwtCustomClaims.NICKNAME.getText())).thenReturn(MOCK_NICKNAME);

        Car car = new Car(carDto, jwt);

        assertEquals(DEFAULT_NAME, car.name);
        assertEquals(DEFAULT_DESCRIPTION, car.description);
        assertEquals(CarStep.DRAFT, car.step);
        assertNotNull(car.authors);
        assertEquals(MOCK_UID, car.uploaderId);
        assertEquals(MOCK_NICKNAME, car.uploaderName);
    }
}
