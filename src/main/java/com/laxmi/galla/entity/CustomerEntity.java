package com.laxmi.galla.entity;

import com.laxmi.galla.core.model.AuditableEntity;
import com.laxmi.galla.enums.AccountStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer_entity", indexes = {
        @Index(name = "idx_customer_contact", columnList = "contact")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity extends AuditableEntity<String> {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Size(max = 255, message = "Address too long")
    private String address;

    @Pattern(regexp = "[A-Z0-9]{10}", message = "PAN must be 10 characters")
    @Column(name = "pan_number", unique = true)
    private String panNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    private String blockReason;
    private String blockedBy;
    private Instant blockedAt;

    private String deactivatedReason;
    private String deactivatedBy;
    private Instant deactivatedAt;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "customer_category",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    protected void normalize() {
        if (address != null) address = address.trim();
        if (panNumber != null) panNumber = panNumber.trim().toUpperCase();

        if (status == null) {
            status = AccountStatus.ACTIVE;
        }
    }

    public boolean isBlocked() {
        return this.status == AccountStatus.BLOCKED;
    }

    public void block(String reason, String performedBy){
        if(isBlocked()){
            return;
        }
        ensureCanBeBlocked();

        this.status = AccountStatus.BLOCKED;
        this.blockReason = reason;
        this.blockedBy = performedBy;
        this.blockedAt = Instant.now();
    }

    private void ensureCanBeBlocked(){
        if (isDeleted()) {
            throw new IllegalStateException("Cannot block a deleted customer");
        }
    }

    // Inside CustomerEntity.java

    /**
     * Atomic domain operation: Deactivate this customer
     */
    public void deactivate(String reason, String performedBy) {
        if (isDeleted()) {
            throw new IllegalStateException("Cannot deactivate a deleted customer");
        }

        if (this.status == AccountStatus.INACTIVE) {
            throw new IllegalStateException("Customer is already deactivated");
        }

        this.status = AccountStatus.INACTIVE;
        // Optional audit fields (highly recommended for enterprise traceability)
         this.deactivatedReason = reason;
         this.deactivatedBy = performedBy;
         this.deactivatedAt = Instant.now();
    }

    public void activate(String reason, String performedBy) {

        if (isDeleted()) {
            throw new IllegalStateException(
                    "Cannot activate a deleted customer"
            );
        }

        if (this.status == AccountStatus.BLOCKED) {
            throw new IllegalStateException(
                    "Blocked customer must be restored first"
            );
        }

        if (this.status == AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Customer is already active"
            );
        }

        this.status = AccountStatus.ACTIVE;
    }
}