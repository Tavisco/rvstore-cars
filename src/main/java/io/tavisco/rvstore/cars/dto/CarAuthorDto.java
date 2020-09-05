package io.tavisco.rvstore.cars.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CarAuthorDto {

    @NotBlank
    @Size(min = 3, max = 255)
    String name;

}
