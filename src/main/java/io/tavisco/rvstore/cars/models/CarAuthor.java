package io.tavisco.rvstore.cars.models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.tavisco.rvstore.common.AbstractAuthor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Author
 */
@Entity
@FieldDefaults(level = AccessLevel.PUBLIC)
@NoArgsConstructor
@Table(name = "w_car_authors")
public class CarAuthor extends AbstractAuthor {

    @ManyToOne
    @JoinColumn(name="car_id", nullable=false)
    Car car;
    
}