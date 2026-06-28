package com.laxmi.galla.controller;

import com.laxmi.galla.company.application.ICompanyService;
import com.laxmi.galla.core.dto.response.ApiResult;
import com.laxmi.galla.customer.dto.response.CustomerResponseDto;
import com.laxmi.galla.dto.request.CompanyRequestDto;
import com.laxmi.galla.company.dto.response.CompanyResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final ICompanyService companyService;

    @PostMapping
    public ApiResult<CompanyResponseDto> createCompany(@RequestBody CompanyRequestDto dto) {
        CustomerResponseDto response = companyService.createCustomer(dto);
        return ApiResult.created(response).toBuilder().message("Customer creation completed").build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<CompanyResponseDto>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable Long id) {
        Optional<CompanyResponseDto> companyOpt = companyService.getCustomerById(id);
        return companyOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> updateCompany(@PathVariable Long id,
                                                            @RequestBody CompanyRequestDto dto) {
        Optional<CompanyResponseDto> updatedOpt = companyService.updateCustomer(id, dto);
        return updatedOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        boolean deleted = companyService.deleteCustomer(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Optional: Pagination endpoint
//    @GetMapping
//    public ResponseEntity<PaginatedResponse<CompanyResponseDto>> getCompaniesPaginated(Pageable pageable) {
//        return ResponseEntity.ok(companyService.getCompaniesPaginated(pageable));
//    }
}
