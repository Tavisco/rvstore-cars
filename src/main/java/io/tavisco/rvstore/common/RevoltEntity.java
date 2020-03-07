package io.tavisco.rvstore.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * RevoltEntity
 */
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PUBLIC)
public abstract class RevoltEntity extends PanacheEntityBase {

    @Column(name = "name", columnDefinition = "TEXT")
    String name;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;


    
}