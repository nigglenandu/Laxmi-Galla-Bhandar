package com.laxmi.galla.services;

import com.laxmi.galla.core.exception.ResourceNotFoundException;
import com.laxmi.galla.core.pagination.PageResponse;
import com.laxmi.galla.core.pagination.PageResponseFactory;
import com.laxmi.galla.core.pagination.PaginationPolicy;
import com.laxmi.galla.core.security.context.AuthContext;
import com.laxmi.galla.dto.CustomerSearchCriteria;
import com.laxmi.galla.dto.PaginatedResponse;
import com.laxmi.galla.dto.request.CustomerRequestDto;
import com.laxmi.galla.dto.response.CustomerResponseDto;
import com.laxmi.galla.entity.Category;
import com.laxmi.galla.entity.CustomerEntity;
import com.laxmi.galla.mapper.CustomerMapper;
import com.laxmi.galla.repository.CategoryRepository;
import com.laxmi.galla.repository.CustomerRepository;
import com.laxmi.galla.specification.CustomerSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final ICategoryService categoryService; // must have a method to return Category entity
    private final CustomerMapper customerMapper;
    private final CategoryRepository categoryRepository;
    private final AuthContext authContext;
    private final PaginationPolicy paginationPolicy;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               ICategoryService categoryService,
                               CustomerMapper customerMapper, CategoryRepository categoryRepository, AuthContext authContext, PaginationPolicy paginationPolicy) {
        this.customerRepository = customerRepository;
        this.categoryService = categoryService;
        this.customerMapper = customerMapper;
        this.categoryRepository = categoryRepository;
        this.authContext = authContext;
        this.paginationPolicy = paginationPolicy;
    }

    @Override
    public CustomerResponseDto createCustomer(CustomerRequestDto dto) {
        CustomerEntity customer = customerMapper.toCustomerEntity(dto);
        // Fetch actual Category entities by IDs
        Set<Category> categories = fetchCategoryEntitiesByIds(dto.categoryIds());
        customer.setCategories(categories);
        CustomerEntity saved = customerRepository.save(customer);
        return customerMapper.toCustomerResponseDto(saved);
    }

    @Override
    public PageResponse<CustomerResponseDto> getAllCustomers(
            CustomerSearchCriteria criteria, Pageable pageable
    ) {

        Pageable safePageable = paginationPolicy.apply(pageable);

        Specification<CustomerEntity> spec = CustomerSpecification.withCriteria(criteria);

        Page<CustomerEntity> page = customerRepository.findAll(spec, safePageable);

        return PageResponseFactory.fromPage(page, customerMapper::toCustomerResponseDto);
    }


    @Override
    public CustomerResponseDto getCurrentCustomerProfile() {
        Long userId = authContext.getUserId();

        return customerRepository.findById(userId).
                map(customerMapper::toCustomerResponseDto).
                orElseThrow(() -> new ResourceNotFoundException("Customer", userId.toString()));
    }

//    @Override
//    public Optional<CustomerResponseDto> updateCustomer(Long id, CustomerRequestDto dto) {
//        return customerRepository.findById(id)
//                .map(existing -> {
//                    existing.setName(dto.name());
//                    existing.setContact(dto.contact());
//                    existing.setAddress(dto.address());
//                    existing.setPanNo(dto.panNo());
//                    existing.setCategories(fetchCategoryEntitiesByIds(dto.categoryIds()));
//                    CustomerEntity updated = customerRepository.save(existing);
//                    return customerMapper.toCustomerResponseDto(updated);
//                });
//    }

    @Override
    public boolean deleteCustomer(Long id) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customerRepository.delete(customer);
                    return true;
                }).orElse(false);
    }

//    @Override
//    public PaginatedResponse<CustomerResponseDto> getCustomersPaginated(Pageable pageable) {
//        Page<CustomerEntity> page = customerRepository.findAll(pageable);
//        List<CustomerResponseDto> content = page.getContent()
//                .stream()
//                .map(customerMapper::toCustomerResponseDto)
//                .collect(Collectors.toList());
//
//        return new PaginatedResponse<>(
//                content,
//                page.isLast(),
//                page.getNumber(),
//                page.getSize(),
//                page.getTotalElements(),
//                page.getTotalPages()
//        );
//    }

    // 2. Use this service method
    @Override
    public PaginatedResponse<CustomerResponseDto> getCustomersPaginated(Pageable pageable) {
        return PaginatedResponse.fromPage(
                customerRepository.findAll(pageable)
                        .map(customerMapper::toCustomerResponseDto)
        );
    }

    private Set<Category> fetchCategoryEntitiesByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        Set<Category> categories =
                new HashSet<>(categoryRepository.findAllById(ids));

        if (categories.size() != ids.size()) {
            throw new ResourceNotFoundException("Category", "ids");
        }

        return categories;
    }
}
