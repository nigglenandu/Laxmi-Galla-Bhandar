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
    /**
     * Domain behavior - controlled status transition.
     * Extend this with a full state machine (Spring State Machine) when complexity grows.
     */
//    public void changeStatus(AccountStatus newStatus) {
//        if (this.status == AccountStatus.DELETED) {
//            throw new IllegalStateException("Cannot modify status of a deleted customer");
//        }
//        // Add business rules here (e.g., BLOCKED → only by compliance role, etc.)
//        this.status = newStatus;
//    }

    /**
     * Explicit soft delete method (consistent with @SQLDelete).
     */
//    public void softDelete() {
//        this.status = AccountStatus.DELETED;
//    }

    /**
     * Convenience methods for common status checks.
     */
//    public boolean isActive() {
//        return this.status == AccountStatus.ACTIVE;
//    }


}