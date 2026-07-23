package com.laxmi.galla.categories.repository;

import com.laxmi.galla.categories.domain.entity.Category;
import com.laxmi.galla.company.domain.entity.Company;
import com.laxmi.galla.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
//    Optional<User> findByEmail(String email);
//    Optional<Category> findByIdIncludingDeleted(Long id);

    @Query("""
        SELECT c
        FROM Category c
        WHERE c.id = :id
    """)
    Optional<Category> findByIdIncludingDeleted(@Param("id") Long id);
}
