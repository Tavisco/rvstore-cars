package io.tavisco.rvstore.cars.models;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id", updatable = false)
    Long id;
    
    @Column(name = "name", columnDefinition = "TEXT")
    String name;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Transient
    private byte[] zipFile = null;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    Set<CarAuthor> authors;

    public static List<Car> findByName(String nameFind) {
        return find("name LIKE :nameFind", Parameters.with("nameFind", "%".concat(nameFind).concat("%"))).list();
    }
}