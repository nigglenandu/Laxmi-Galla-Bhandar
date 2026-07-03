package com.laxmi.galla.company.application;

import com.laxmi.galla.company.dto.request.CompanyRequestDto;
import com.laxmi.galla.company.dto.request.CompanySearchCriteria;
import com.laxmi.galla.company.dto.response.CompanyResponseDto;
import com.laxmi.galla.core.pagination.PageResponse;
import com.laxmi.galla.customer.dto.request.CustomerSearchCriteria;
import com.laxmi.galla.customer.dto.response.CustomerResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ICompanyService {
    CompanyResponseDto createCompany(CompanyRequestDto dto);

    // Get all companies as response DTOs
    PageResponse<CompanyResponseDto> getAllCompanies(CompanySearchCriteria criteria,
                                                     Pageable pageable);

    CompanyResponseDto getCurrentCompanyProfile();

    CompanyResponseDto updateCompany(Long id, CompanyRequestDto dto);

    CompanyResponseDto updateMyCompany(CompanyRequestDto dto);

    boolean deleteCustomer(Long id);

//    PaginatedResponse<CompanyResponseDto> getCompaniesPaginated(Pageable pageable);
}
