package io.tavisco.rvstore.cars;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Car
 */
@Entity
@FieldDefaults(level = AccessLevel.PUBLIC)
@NoArgsConstructor
@Table(name = "w_cars")
public class Car extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "car_generator")
    @SequenceGenerator(name = "car_generator", sequenceName = "cars_seq", allocationSize = 50)
    @Column(name = "car_id", updatable = false, nullable = false)
    Long id;

    @Column(name = "name", columnDefinition = "TEXT")
    String name;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    public static List<Car> findByName(String nameFind) {
        return find("name LIKE :nameFind", Parameters.with("nameFind", "%".concat(nameFind).concat("%"))).list();
    }
}