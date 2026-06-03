package com.laxmi.galla.controller;

import com.laxmi.galla.core.dto.response.ApiResult;
import com.laxmi.galla.dto.PaginatedResponse;
import com.laxmi.galla.dto.request.CustomerRequestDto;
import com.laxmi.galla.dto.response.CustomerResponseDto;
import com.laxmi.galla.services.ICustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
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

//    // Get all customers
//    @GetMapping("/all")
//    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
//        List<CustomerResponseDto> response = customerService.getAllCustomers();
//        return ResponseEntity.ok(response);
//    }

    // Get customer by ID
    @GetMapping("/me")
    public ApiResult<CustomerResponseDto> getMyProfile() {
        return ApiResult.ok(customerService.getCurrentCustomerProfile());
    }

//    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ApiResult<UserResponseDto> getUserById(@PathVariable Long id) {
//        return ApiResult.ok(userService.getUserById(id));
//    }

    // Update customer
//    @PutMapping("/{id}")
//    public ResponseEntity<CustomerResponseDto> updateCustomer(
//            @PathVariable Long id,
//            @RequestBody CustomerRequestDto dto
//    ) {
//        return customerService.updateCustomer(id, dto)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    // Delete customer
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerService.deleteCustomer(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Paginated customers
    @GetMapping
    public ResponseEntity<PaginatedResponse<CustomerResponseDto>> getCustomersPaginated(Pageable pageable) {
        PaginatedResponse<CustomerResponseDto> response = customerService.getCustomersPaginated(pageable);
        return ResponseEntity.ok(response);
    }
}
