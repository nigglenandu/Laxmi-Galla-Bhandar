package com.laxmi.galla.company.respository;

import com.laxmi.galla.company.domain.entity.Company;
import com.laxmi.galla.customer.domain.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

    boolean existsByPanNumberAndIdNot(String panNumber, Long id);

    @Query("""
        SELECT c
        FROM Company c
        WHERE c.id = :id
    """)
    Optional<Company> findByIdIncludingDeleted(@Param("id") Long id);
}

