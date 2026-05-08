//package com.laxmi.galla.services;
//
//import com.laxmi.galla.dto.PaginatedResponse;
//import com.laxmi.galla.dto.request.CustomerRequestDto;
//import com.laxmi.galla.dto.response.CustomerResponseDto;
//import com.laxmi.galla.entity.Category;
//import com.laxmi.galla.entity.CustomerEntity;
//import com.laxmi.galla.mapper.CustomerMapper;
//import com.laxmi.galla.repository.CustomerRepository;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class CustomerServiceImpl implements ICustomerService {
//
//    private final CustomerRepository customerRepository;
//    private final ICategoryService categoryService; // must have a method to return Category entity
//    private final CustomerMapper customerMapper;
//
//    public CustomerServiceImpl(CustomerRepository customerRepository,
//                               ICategoryService categoryService,
//                               CustomerMapper customerMapper) {
//        this.customerRepository = customerRepository;
//        this.categoryService = categoryService;
//        this.customerMapper = customerMapper;
//    }
//
//    @Override
//    public CustomerResponseDto createCustomer(CustomerRequestDto dto) {
//        CustomerEntity customer = customerMapper.toCustomerEntity(dto);
//        // Fetch actual Category entities by IDs
//        Set<Category> categories = fetchCategoryEntitiesByIds(dto.categoryIds());
//        customer.setCategories(categories);
//        CustomerEntity saved = customerRepository.save(customer);
//        return customerMapper.toCustomerResponseDto(saved);
//    }
//
//    @Override
//    public List<CustomerResponseDto> getAllCustomers() {
//        return customerRepository.findAll()
//                .stream()
//                .map(customerMapper::toCustomerResponseDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public Optional<CustomerResponseDto> getCustomerById(Long id) {
//        return customerRepository.findById(id)
//                .map(customerMapper::toCustomerResponseDto);
//    }
//
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
//
//    @Override
//    public boolean deleteCustomer(Long id) {
//        return customerRepository.findById(id)
//                .map(customer -> {
//                    customerRepository.delete(customer);
//                    return true;
//                }).orElse(false);
//    }
//
////    @Override
////    public PaginatedResponse<CustomerResponseDto> getCustomersPaginated(Pageable pageable) {
////        Page<CustomerEntity> page = customerRepository.findAll(pageable);
////        List<CustomerResponseDto> content = page.getContent()
////                .stream()
////                .map(customerMapper::toCustomerResponseDto)
////                .collect(Collectors.toList());
////
////        return new PaginatedResponse<>(
////                content,
////                page.isLast(),
////                page.getNumber(),
////                page.getSize(),
////                page.getTotalElements(),
////                page.getTotalPages()
////        );
////    }
//
//    // 2. Use this service method
//    @Override
//    public PaginatedResponse<CustomerResponseDto> getCustomersPaginated(Pageable pageable) {
//        return PaginatedResponse.fromPage(
//                customerRepository.findAll(pageable)
//                        .map(customerMapper::toCustomerResponseDto)
//        );
//    }
//
//    // NEW: Fetch actual Category entities for Customer
//    private Set<Category> fetchCategoryEntitiesByIds(Set<Long> ids) {
//        if (ids == null || ids.isEmpty()) return new HashSet<>();
//        return ids.stream()
//                .map(categoryService::getCategoryEntityById) // must return Optional<Category>
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .collect(Collectors.toSet());
//    }
//}
