package com.laxmi.galla.customer.application;

import com.laxmi.galla.core.pagination.PageResponse;
import com.laxmi.galla.customer.dto.request.CustomerSearchCriteria;
import com.laxmi.galla.dto.PaginatedResponse;
import com.laxmi.galla.customer.dto.request.CustomerRequestDto;
import com.laxmi.galla.customer.dto.response.CustomerResponseDto;
import com.laxmi.galla.customer.domain.enums.AccountAction;
import org.springframework.data.domain.Pageable;

public interface ICustomerService {

    // Create a customer from request DTO and return response DTO
    CustomerResponseDto createCustomer(CustomerRequestDto dto);

    // Get all customers as response DTOs
    PageResponse<CustomerResponseDto> getAllCustomers(CustomerSearchCriteria criteria,
                                                      Pageable pageable);

    CustomerResponseDto getCurrentCustomerProfile();

    CustomerResponseDto updateCustomer(Long id, CustomerRequestDto dto);

    CustomerResponseDto updateMyProfile(CustomerRequestDto dto);

    void performAccountAction(Long customerId, AccountAction action, String reason);


    // Get paginated customers as response DTOs
    PaginatedResponse<CustomerResponseDto> getCustomersPaginated(Pageable pageable);
}
