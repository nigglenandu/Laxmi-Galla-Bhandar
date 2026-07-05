package com.laxmi.galla.categories.respository;

import com.laxmi.galla.categories.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

    Optional<Category> findByIdIncludingDeleted(Long id);
}
