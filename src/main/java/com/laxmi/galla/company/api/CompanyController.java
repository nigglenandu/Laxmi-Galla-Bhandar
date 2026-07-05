package com.laxmi.galla.controller;

import com.laxmi.galla.company.application.ICompanyService;
import com.laxmi.galla.company.dto.request.CompanyActionRequest;
import com.laxmi.galla.company.dto.request.CompanyRequestDto;
import com.laxmi.galla.company.dto.request.CompanySearchCriteria;
import com.laxmi.galla.core.dto.response.ApiResult;
import com.laxmi.galla.core.pagination.PageResponse;
import com.laxmi.galla.core.pagination.PageResponseAssembler;
import com.laxmi.galla.company.dto.response.CompanyResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company")
public class CompanyController {

    private final ICompanyService companyService;

    @PostMapping
    public ApiResult<CompanyResponseDto> createCompany(@RequestBody CompanyRequestDto dto) {
        CompanyResponseDto response = companyService.createCompany(dto);
        return ApiResult.created(response).toBuilder().message("Company creation completed").build();
    }

    @GetMapping
    public ApiResult<PageResponse<CompanyResponseDto>> getAllCompanies(
            @ParameterObject Pageable pageable,
            @ModelAttribute CompanySearchCriteria criteria
            ) {
        PageResponse<CompanyResponseDto> pageResponse = companyService.getAllCompanies(criteria, pageable);
        PageResponse<CompanyResponseDto> enriched = PageResponseAssembler.of(pageResponse)
                .withLinks("/api/v1/companies")
                .withMetadata("criteria", criteria)
                .assemble();
        return ApiResult.ok(enriched);
    }

    @GetMapping()
    public ApiResult<CompanyResponseDto> getMyProfile() {
       return ApiResult.ok(companyService.getCurrentCompanyProfile());
    }

    @PutMapping("/{id}")
    public ApiResult<CompanyResponseDto> updateCompany(@PathVariable Long id,
                                                            @Valid @RequestBody CompanyRequestDto dto) {
       CompanyResponseDto response = companyService.updateCompany(id, dto);
       return ApiResult.ok(response)
               .toBuilder()
               .message("Company updated successfully")
               .build();
    }

    @PutMapping("/me")
    public ApiResult<CompanyResponseDto> updateMyCompany(@Valid @RequestBody CompanyRequestDto dto) {
        CompanyResponseDto response = companyService.updateMyCompany(dto);
        return ApiResult.ok(response).toBuilder().message("Company updated successfully").build();
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyActionRequest request) {

        companyService.deleteCompany(id, request);

        return ApiResult.noContent()
                .toBuilder()
                .message("Company deleted successfully")
                .build();
    }

    @PatchMapping("/{id}/restore")
    public ApiResult<Void> restoreCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyActionRequest request) {

        companyService.restoreCompany(id, request);

        return ApiResult.<Void>ok(null)
                .toBuilder()
                .message("Company restored successfully")
                .build();
    }

    // Optional: Pagination endpoint
//    @GetMapping
//    public ResponseEntity<PaginatedResponse<CompanyResponseDto>> getCompaniesPaginated(Pageable pageable) {
//        return ResponseEntity.ok(companyService.getCompaniesPaginated(pageable));
//    }
}
