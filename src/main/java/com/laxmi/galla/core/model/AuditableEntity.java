package com.laxmi.galla.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Base class for all auditable entities with:
 * - Created/updated timestamps & users (Spring Data JPA auditing)
 * - Soft-delete (deletedAt/deletedBy)
 * - Optimistic locking (@Version)
 * - Convenience methods: isActive(), isDeleted(), isNew(), markAsDeleted(), restore()
 * - Protection against hard delete (@PreRemove)
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter(AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AuditableEntity<U> extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    U createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    U updatedBy;

    @Column(name = "deleted_at", nullable = true)
    Instant deletedAt;

    @Column(name = "deleted_by", nullable = true)
    U deletedBy;

    @Version
    Long version;

    @Transient
    public boolean isDeleted() {
        return deletedAt != null;
    }

    @Transient
    public boolean isActive() {
        return deletedAt == null;
    }

    @Transient
    public boolean isNew() {
        return getId() == null;
    }

    /**
     * Soft-delete this entity (only if currently active).
     */
    public void markAsDeleted(U deleter) {
        if (isDeleted()) {
            throw new IllegalStateException(
                    getClass().getSimpleName() + " is already deleted"
            );
        }
        if (isActive()) {
            this.deletedAt = Instant.now();
            this.deletedBy = deleter;
        }
    }

    /**
     * Restore soft-deleted entity (undelete).
     */
    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    @PreRemove
    protected void onRemove() {
        throw new UnsupportedOperationException(
                "Hard delete is not allowed. Use soft-delete (markAsDeleted) instead.");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + getId() + ", deleted=" + isDeleted() + "]";
    }
}