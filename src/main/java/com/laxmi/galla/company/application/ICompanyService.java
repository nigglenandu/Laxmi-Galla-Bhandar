package com.laxmi.galla.company.application;

import com.laxmi.galla.dto.request.CompanyRequestDto;
import com.laxmi.galla.company.dto.response.CompanyResponseDto;

import java.util.List;
import java.util.Optional;

public interface ICompanyService {
    CompanyResponseDto createCustomer(CompanyRequestDto dto);

    List<CompanyResponseDto> getAllCustomers();

    Optional<CompanyResponseDto> getCustomerById(Long id);

    Optional<CompanyResponseDto> updateCustomer(Long id, CompanyRequestDto dto);

    boolean deleteCustomer(Long id);

//    PaginatedResponse<CompanyResponseDto> getCompaniesPaginated(Pageable pageable);
}
