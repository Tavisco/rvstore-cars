package io.tavisco.rvstore.cars.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import io.tavisco.rvstore.cars.dto.CarAuthorDto;
import io.tavisco.rvstore.cars.dto.CarDto;
import io.tavisco.rvstore.cars.enums.JwtCustomClaims;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Car
 */
@Entity
@FieldDefaults(level = AccessLevel.PUBLIC)
@Table(name = "w_cars")
@NoArgsConstructor
public class Car {

    @Id
    @SequenceGenerator(name="car_id_generator", sequenceName = "w_cars_car_id_seq", allocationSize=50)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "car_id_generator")
    @Column(name = "car_id", nullable = false, updatable = false)
    Long id;

    //@NotBlank
    @Column(name = "name", columnDefinition = "TEXT")
    String name;

    //@NotBlank
    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    //@NotBlank
    @Column(name = "uploader_id", columnDefinition = "TEXT")
    String uploaderId;

    //@NotBlank
    @Column(name = "uploader_name", columnDefinition = "TEXT")
    String uploaderName;

    @Column(name = "create_date")
    @CreationTimestamp
    LocalDateTime createDate;

    @Column(name = "update_date")
    @UpdateTimestamp
    LocalDateTime updateDate;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<CarAuthor> authors;

    public Car(CarDto carDto, JsonWebToken jwt) {
        this.name = carDto.getName();
        this.description = carDto.getDescription();
        this.authors = carDto.getAuthors().stream()
                                            .map(dto -> new CarAuthor(dto, this))
                                            .collect(Collectors.toList());
        this.uploaderId = jwt.getClaim(JwtCustomClaims.UID.getText());
        this.uploaderName = jwt.getClaim(JwtCustomClaims.NICKNAME.getText());
    }
}