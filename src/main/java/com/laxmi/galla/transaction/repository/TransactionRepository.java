package com.laxmi.galla.transaction.repository;

import com.laxmi.galla.transaction.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("""
        SELECT c
        FROM Transaction c
        WHERE c.id = :id
    """)
    Optional<Transaction> findByIdIncludingDeleted(@Param("id") Long id);
}