package com.laxmi.galla.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.laxmi.galla.entity.enums.PurchaseOrSale;
import core.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_info", indexes = {
        @Index(name = "idx_transaction_customer", columnList = "customer_id"),
        @Index(name = "idx_transaction_date", columnList = "transaction_date")
})
@SQLDelete(sql = "UPDATE transaction_info SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class TransactionInfo extends AuditableEntity<String> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonBackReference
    private Company company;

    @Column(name = "unit_amount", precision = 19, scale = 4)
    private BigDecimal unitAmount;

    @Column(name = "transaction_date")
    private LocalDateTime date;

    private String description;

    @Column(name = "due_amount", precision = 19, scale = 4)
    private BigDecimal dueAmount;

    @Column(name = "total_amount", precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(precision = 19, scale = 4)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "purchase_or_sale")
    private PurchaseOrSale purchaseOrSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private CustomUnit unit;
}
