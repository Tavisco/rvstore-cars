package io.tavisco.rvstore.cars.models;

import io.tavisco.rvstore.cars.dto.CarAuthorDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;

/**
 * Author
 */
@Entity
@FieldDefaults(level = AccessLevel.PUBLIC)
@Table(name = "w_car_authors")
@NoArgsConstructor
public class CarAuthor {

    @Id
    @SequenceGenerator(name="author_id_generator", sequenceName = "w_car_authors_author_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_id_generator")
    @Column(name = "author_id", nullable = false, updatable = false)
    Long id;

    @JsonbTransient
    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    Car car;

    @Column(name = "name", columnDefinition = "TEXT")
    String name;

    public CarAuthor(CarAuthorDto authorDto, Car car) {
        this.name = authorDto.getName();
        this.car = car;
    }
}