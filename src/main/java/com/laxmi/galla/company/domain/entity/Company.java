package com.laxmi.galla.company.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.laxmi.galla.core.model.AuditableEntity;
import com.laxmi.galla.entity.TransactionInfo;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class Company extends AuditableEntity<String> {

    @Column(nullable = false)
    private String name;

    @Column(name = "pan_no")
    private String panNumber;

    @Column(nullable = false, unique = true)
    String phoneNo;

    @Column(name = "company_address")
    private String address;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private Set<TransactionInfo> transactions = new HashSet<>();
}
