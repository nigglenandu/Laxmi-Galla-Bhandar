package com.laxmi.galla.entity;

import com.laxmi.galla.core.model.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer_entity", indexes = {
        @Index(name = "idx_customer_contact", columnList = "contact")
})
@SQLDelete(sql = "UPDATE customer_entity SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity extends AuditableEntity<String> {

    @Column(nullable = false)
    String firstName;

    @Column(nullable = false)
    String lastName;

    @Size(max = 255, message = "Address too long")
    private String address;

    @Pattern(regexp = "[A-Z0-9]{10}", message = "PAN must be 10 characters")
    @Column(name = "pan_number", unique = true)
    private String panNumber;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "customer_category",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @PrePersist
    @PreUpdate
    public void normalize() {
        if (firstName != null) firstName = firstName.trim();
        if (lastName != null) lastName = lastName.trim();
        if (address != null) address = address.trim();
        if (panNumber != null) panNumber = panNumber.trim().toUpperCase();
    }
}