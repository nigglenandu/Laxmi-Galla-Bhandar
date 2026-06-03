package com.laxmi.galla.services;

import com.laxmi.galla.dto.PaginatedResponse;
import com.laxmi.galla.dto.request.CustomerRequestDto;
import com.laxmi.galla.dto.response.CustomerResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {

    // Create a customer from request DTO and return response DTO
    CustomerResponseDto createCustomer(CustomerRequestDto dto);

    // Get all customers as response DTOs
//    List<CustomerResponseDto> getAllCustomers();

    // Get customer by ID as response DTO
//    Optional<CustomerResponseDto> getCustomerById(Long id);

    // Update customer by ID using request DTO, return updated response DTO
//    Optional<CustomerResponseDto> updateCustomer(Long id, CustomerRequestDto dto);

    CustomerResponseDto getCurrentCustomerProfile();

    // Delete customer by ID
    boolean deleteCustomer(Long id);

    // Get paginated customers as response DTOs
    PaginatedResponse<CustomerResponseDto> getCustomersPaginated(Pageable pageable);
}
