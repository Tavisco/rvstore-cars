package io.tavisco.rvstore.common;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * AbstractAuthor
 */
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PUBLIC)
public abstract class AbstractAuthor extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id", updatable = false)
    Long id;

    @Column(name = "name", columnDefinition = "TEXT")
    String name;
    
}