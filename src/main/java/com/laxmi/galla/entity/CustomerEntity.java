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

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name too long")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Contact cannot be blank")
    @Pattern(regexp = "(\\+977|00977)?9\\d{8}", message = "Contact must be 10 digits")
    @Column(nullable = false, unique = true)
    private String contact;

    @Size(max = 255, message = "Address too long")
    private String address;

    @Pattern(regexp = "[A-Z0-9]{10}", message = "PAN must be 10 characters")
    @Column(name = "pan_no", unique = true)
    private String panNo;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "customer_category",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

}