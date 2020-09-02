package io.tavisco.rvstore.cars.models;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Car
 */
@Entity
@FieldDefaults(level = AccessLevel.PUBLIC)
@NoArgsConstructor
@Table(name = "w_cars")
@AllArgsConstructor
@Builder
public class Car extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id", updatable = false)
    Long id;
    
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "name", columnDefinition = "TEXT")
    String name;

    @NotBlank
    @Size(min = 3, max = 255)
    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Transient
    private byte[] zipFile = null;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) 
    List<CarAuthor> authors;

    public static List<Car> findByName(String nameFind) {
        return find("name LIKE :nameFind", Parameters.with("nameFind", "%".concat(nameFind).concat("%"))).list();
    }
}