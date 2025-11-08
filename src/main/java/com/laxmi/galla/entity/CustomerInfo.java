package com.laxmi.galla.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer_info", indexes = {
        @Index(name = "idx_customer_contact", columnList = "contact")
})
@SQLDelete(sql = "UPDATE customer_info SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class CustomerInfo extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String contact;

    private String address;

    @Column(name = "pan_no")
    private String panNo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "customer_category",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

}
