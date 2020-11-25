package io.tavisco.rvstore.cars.enums;

import javax.persistence.AttributeConverter;
import java.util.Arrays;

public enum CarStep {
    PENDING_UPLOAD("PENDING_UPLOAD"),
    DRAFT("DRAFT"),
    PUBLISHED("PUBLISHED");

    private final String description;

    CarStep(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static class Mapper implements AttributeConverter<CarStep, String> {

        @Override
        public String convertToDatabaseColumn(CarStep attribute) {
            return String.valueOf(attribute.getDescription());
        }

        @Override
        public CarStep convertToEntityAttribute(String dbData) {
            return Arrays.stream(CarStep.values()).filter(x -> x.getDescription().equals(dbData)).findFirst().orElseThrow(() -> new IllegalStateException("Invalid enum value: " + dbData));
        }
    }
}
