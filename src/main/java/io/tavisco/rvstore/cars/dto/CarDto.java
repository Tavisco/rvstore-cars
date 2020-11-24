package io.tavisco.rvstore.cars.dto;

import io.tavisco.rvstore.cars.enums.CarStep;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {

    @NotBlank
    @Size(min = 3, max = 50)
    String name;

    @NotBlank
    @Size(min = 3, max = 255)
    String description;

    @NotNull
    CarStep step;

    List<CarAuthorDto> authors;

}
