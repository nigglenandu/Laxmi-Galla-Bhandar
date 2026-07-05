package com.laxmi.galla.categories.domain.entity;

import com.laxmi.galla.core.model.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "category")
@SQLDelete(sql = "UPDATE category SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class Category extends AuditableEntity<String> {

    @NotBlank(message = "Category name cannot be blank")
    @Size(max = 100, message = "Category name too long")
    @Column(nullable = false, unique = true)
    private String name;

    public void delete(String deletedBy) {
        markAsDeleted(deletedBy);
    }

    public void restoreCategory() {
        if (!isDeleted()) {
            throw new IllegalStateException("Category is not deleted.");
        }
        restore();
    }
}
