package io.tavisco.rvstore.cars.models;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.tavisco.rvstore.cars.dto.CarAuthorDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Author
 */
@Entity
@FieldDefaults(level = AccessLevel.PUBLIC)
@Table(name = "w_car_authors")
@NoArgsConstructor
public class CarAuthor {

    @Id
    @SequenceGenerator(name="author_id_generator", sequenceName = "w_car_authors_author_id_seq", allocationSize=50)
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