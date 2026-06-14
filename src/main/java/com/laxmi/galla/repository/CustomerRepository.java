package com.laxmi.galla.repository;

import com.laxmi.galla.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long>, JpaSpecificationExecutor<CustomerEntity> {
    boolean existsByPanNumberAndIdNot(String panNumber, Long id);
}