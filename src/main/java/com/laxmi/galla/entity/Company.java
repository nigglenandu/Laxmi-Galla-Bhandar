package com.laxmi.galla.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "company")
@SQLDelete(sql = "UPDATE company SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Company extends BaseEntity{

    @Column(nullable = false)
    private String name;

    @Column(name = "pan_no")
    private String panNo;

    @Column(name = "company_address")
    private String companyAddress;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private Set<TransactionInfo> transactions = new HashSet<>();
}
