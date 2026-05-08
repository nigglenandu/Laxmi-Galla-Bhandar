package com.laxmi.galla.controller;

import com.laxmi.galla.dto.PaginatedResponse;
import com.laxmi.galla.dto.request.CustomerRequestDto;
import com.laxmi.galla.dto.response.CustomerResponseDto;
import com.laxmi.galla.services.ICustomerService;
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
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody CustomerRequestDto dto) {
        CustomerResponseDto response = customerService.createCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all customers
    @GetMapping("/all")
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
        List<CustomerResponseDto> response = customerService.getAllCustomers();
        return ResponseEntity.ok(response);
    }

    // Get customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update customer
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequestDto dto
    ) {
        return customerService.updateCustomer(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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
