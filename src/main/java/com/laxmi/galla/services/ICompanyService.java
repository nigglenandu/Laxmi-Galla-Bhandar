package com.laxmi.galla.services;

import com.laxmi.galla.dto.*;
import com.laxmi.galla.dto.request.CompanyRequestDto;
import com.laxmi.galla.dto.response.CompanyResponseDto;
import org.springframework.data.domain.Pageable;

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
