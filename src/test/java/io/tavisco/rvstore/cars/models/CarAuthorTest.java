package io.tavisco.rvstore.cars.models;

import io.tavisco.rvstore.cars.dto.CarAuthorDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarAuthorTest {

    private static final String DEFAULT_AUTHOR1_NAME = "Default author one";

    @Test
    void testConstructor() {
        CarAuthorDto dto = CarAuthorDto.builder()
                .name(DEFAULT_AUTHOR1_NAME)
                .build();

        CarAuthor author = new CarAuthor(dto, null);

        assertEquals(DEFAULT_AUTHOR1_NAME, author.name);
    }

}