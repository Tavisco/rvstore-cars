package io.tavisco.rvstore.common;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

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

    @Transient
    private byte[] zipFile = null;

    /**
     * @return the zipFile
     */
    public byte[] getZipFile() {
        return zipFile;
    }
    
}