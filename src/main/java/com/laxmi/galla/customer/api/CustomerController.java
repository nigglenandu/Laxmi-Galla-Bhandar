package com.laxmi.galla.customer.api;

import com.laxmi.galla.core.dto.response.ApiResult;
import com.laxmi.galla.core.pagination.PageResponse;
import com.laxmi.galla.core.pagination.PageResponseAssembler;
import com.laxmi.galla.customer.dto.request.CustomerSearchCriteria;
import com.laxmi.galla.customer.dto.request.CustomerActionRequest;
import com.laxmi.galla.customer.dto.request.CustomerRequestDto;
import com.laxmi.galla.customer.dto.response.CustomerResponseDto;
import com.laxmi.galla.customer.application.ICustomerService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    // Create customer
    @PostMapping
    public ApiResult<CustomerResponseDto> createCustomer(@Valid @RequestBody CustomerRequestDto dto) {
        CustomerResponseDto response = customerService.createCustomer(dto);
        return ApiResult.created(response).toBuilder().message("Customer creation completed").build();
    }

    @GetMapping
    public ApiResult<PageResponse<CustomerResponseDto>> getAllCustomers(
            @ParameterObject Pageable pageable,
            @ModelAttribute CustomerSearchCriteria criteria
    ) {
        PageResponse<CustomerResponseDto> pageResponse = customerService.getAllCustomers(criteria, pageable);
        PageResponse<CustomerResponseDto> enriched = PageResponseAssembler.of(pageResponse)
                .withLinks("/api/v1/customers")
                .withMetadata("criteria", criteria)
                .assemble();

        return ApiResult.ok(enriched);
    }

    // Get customer by ID
    @GetMapping("/me")
    public ApiResult<CustomerResponseDto> getMyProfile() {
        return ApiResult.ok(customerService.getCurrentCustomerProfile());
    }

    @PutMapping("/{id}")
    public ApiResult<CustomerResponseDto> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequestDto dto) {

        CustomerResponseDto response = customerService.updateCustomer(id, dto);
        return ApiResult.ok(response)
                .toBuilder()
                .message("Customer updated successfully")
                .build();
    }

    @PutMapping("/me")
    public ApiResult<CustomerResponseDto> updateMyProfile(@Valid @RequestBody CustomerRequestDto dto){

        CustomerResponseDto response = customerService.updateMyProfile(dto);
                return ApiResult.ok(response)
                .toBuilder()
                .message("Profile updated successfully")
                .build();
    }

    @PostMapping("/{customerId}/actions")
    public ApiResult<Void> performAction(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerActionRequest request
    ) {

        customerService.performAccountAction(
                customerId,
                request.action(),
                request.reason()
        );

        return ApiResult.<Void>ok(null)
                .toBuilder()
                .message("Action completed successfully")
                .build();
    }
}
