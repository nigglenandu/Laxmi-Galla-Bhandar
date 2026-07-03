package com.laxmi.galla.company.respository;

import com.laxmi.galla.company.domain.entity.Company;
import com.laxmi.galla.customer.domain.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

    boolean existsByPanNumberAndIdNot(String panNumber, Long id);
}
