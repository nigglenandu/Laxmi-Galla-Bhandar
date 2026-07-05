package com.laxmi.galla.customer.domain.entity;

import com.laxmi.galla.core.model.AuditableEntity;
import com.laxmi.galla.entity.User;
import com.laxmi.galla.customer.domain.enums.AccountStatus;
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

    @Embedded
    @Builder.Default
    private CustomerActionAudit audit = new CustomerActionAudit();

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

    public void block(String reason, String performedBy) {
        ensureNotDeleted();
        ensureNotStatus(AccountStatus.BLOCKED,
                "Customer already blocked");

        status = AccountStatus.BLOCKED;
        audit.setBlockReason(reason);
        audit.setBlockedBy(performedBy);
        audit.setBlockedAt(Instant.now());
    }

    public void deactivate(String reason, String performedBy) {
        ensureNotDeleted();
        ensureNotStatus(AccountStatus.INACTIVE, "Already deactivated");

        status = AccountStatus.INACTIVE;
        audit.setDeactivatedReason(reason);
        audit.setDeactivatedBy(performedBy);
        audit.setDeactivatedAt(Instant.now());
    }

    public void activate(String reason, String performedBy) {
        ensureNotDeleted();
        ensureNotStatus(AccountStatus.ACTIVE, "Already active");

        if (status == AccountStatus.BLOCKED) {
            throw new IllegalStateException("Must be restored/unblocked first");
        }

        status = AccountStatus.ACTIVE;
        audit.setActivatedReason(reason);
        audit.setActivatedBy(performedBy);
        audit.setActivatedAt(Instant.now());
    }

    // ------------------------
    // GUARDS
    // ------------------------

    private void ensureNotDeleted() {
        if (isDeleted()) {
            throw new IllegalStateException("Customer is deleted");
        }
    }

    private void ensureNotStatus(AccountStatus s, String msg) {
        if (this.status == s) throw new IllegalStateException(msg);
    }

    public void restore(String reason, String performedBy) {

        boolean deleted = isDeleted();
        boolean blocked = status == AccountStatus.BLOCKED;

        if (!deleted && !blocked) {
            throw new IllegalStateException(
                    "Customer cannot be restored"
            );
        }

        if (deleted) {
            super.restore();
        }

        this.status = AccountStatus.ACTIVE;
        audit.setRestoredReason(reason);
        audit.setRestoredBy(performedBy);
        audit.setRestoredAt(Instant.now());
    }

    public boolean isBlocked() {
        return status == AccountStatus.BLOCKED;
    }

    public boolean isInactive() {
        return status == AccountStatus.INACTIVE;
    }
}