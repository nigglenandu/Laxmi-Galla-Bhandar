package com.laxmi.galla.domain.entity;

import com.laxmi.galla.categories.domain.entity.Category;
import com.laxmi.galla.core.model.AuditableEntity;
import com.laxmi.galla.customer.domain.entity.CustomerEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "report")
@SQLDelete(sql = "UPDATE report SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Report extends AuditableEntity<String> {

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "total_credit", precision = 19, scale = 4)
    private BigDecimal totalCredit;

    @Column(name = "total_debit", precision = 19, scale = 4)
    private BigDecimal totalDebit;

    @Column(name = "balance", precision = 19, scale = 4)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
