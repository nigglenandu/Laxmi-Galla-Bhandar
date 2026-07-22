package com.laxmi.galla.domain.entity;

import com.laxmi.galla.core.model.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "custom_unit")
@SQLDelete(sql = "UPDATE custom_unit SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class CustomUnit extends AuditableEntity<String> {

    @Column(name = "unit_name", nullable = false, unique = true)
    private String unitName;
}
