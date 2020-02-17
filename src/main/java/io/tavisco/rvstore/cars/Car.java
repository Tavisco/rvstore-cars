package io.tavisco.rvstore.cars;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
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
@Table(name = "W_CARS")
public class Car extends PanacheEntity {

    @Id
    @GeneratedValue
    @Column(name = "CAR_ID")
    Long id;

    @Column(name = "NAME")
    String name;

    @Column(name = "DESCRIPTION")
    String description;

	public static List<Car> findByName(String nameFind) {
        return find("name LIKE :nameFind", 
                        Parameters.with("nameFind", "%".concat(nameFind).concat("%"))).list();
    }
}