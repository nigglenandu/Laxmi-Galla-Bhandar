package com.laxmi.galla.services;

import com.laxmi.galla.core.exception.DuplicateResourceException;
import com.laxmi.galla.core.exception.ResourceNotFoundException;
import com.laxmi.galla.core.pagination.PageResponse;
import com.laxmi.galla.core.pagination.PageResponseFactory;
import com.laxmi.galla.core.pagination.PaginationPolicy;
import com.laxmi.galla.core.security.context.AuthContext;
import com.laxmi.galla.customer.delete.AccountActionDispatcher;
import com.laxmi.galla.dto.CustomerSearchCriteria;
import com.laxmi.galla.customer.CustomerUpdatedEvent;
import com.laxmi.galla.dto.PaginatedResponse;
import com.laxmi.galla.dto.request.CustomerRequestDto;
import com.laxmi.galla.dto.response.CustomerResponseDto;
import com.laxmi.galla.entity.Category;
import com.laxmi.galla.entity.CustomerEntity;
import com.laxmi.galla.enums.AccountAction;
import com.laxmi.galla.mapper.CustomerMapper;
import com.laxmi.galla.repository.CategoryRepository;
import com.laxmi.galla.repository.CustomerRepository;
import com.laxmi.galla.specification.CustomerSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final ICategoryService categoryService; // must have a method to return Category entity
    private final CustomerMapper customerMapper;
    private final CategoryRepository categoryRepository;
    private final AuthContext authContext;
    private final PaginationPolicy paginationPolicy;
    private final ApplicationEventPublisher eventPublisher;
    private final AccountActionDispatcher accountActionDispatcher;

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

        return customerMapper.toCustomerResponseDto(
                getCustomerOrThrow(authContext.getUserId()));
    }

    @Override
    @Transactional
    public CustomerResponseDto updateCustomer(Long id, CustomerRequestDto dto) {
        CustomerEntity customer = getCustomerOrThrow(id);

        applyUpdate(customer, dto);

        CustomerEntity updated = customerRepository.save(customer);

        String correlationId = Optional.ofNullable(authContext.getCorrelationId())
                .orElse(UUID.randomUUID().toString());

        eventPublisher.publishEvent(
                CustomerUpdatedEvent.of(
                        updated.getId().toString(),
                        correlationId
                )
        );

        return customerMapper.toCustomerResponseDto(updated);
    }

    @Override
    @Transactional
    public CustomerResponseDto updateMyProfile(CustomerRequestDto dto) {

        CustomerEntity customer = getCustomerOrThrow(authContext.getUserId());

        applyUpdate(customer, dto);

        CustomerEntity updated = customerRepository.save(customer);

        String correlationId = Optional.ofNullable(authContext.getCorrelationId())
                .orElse(UUID.randomUUID().toString());

        eventPublisher.publishEvent(
                CustomerUpdatedEvent.of(
                        updated.getId().toString(),
                        correlationId
                )
        );

        return customerMapper.toCustomerResponseDto(updated);
    }

    private void applyUpdate(CustomerEntity customer, CustomerRequestDto dto){
        validatePanUniqueness(customer, dto);
        customerMapper.updateCustomer(dto, customer);
        if(dto.categoryIds() != null){
            Set<Category> categories = fetchCategoryEntitiesByIds(dto.categoryIds());
            customer.setCategories(categories);
        }
    }

    private CustomerEntity getCustomerOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer", id.toString()));
    }


    private void validatePanUniqueness(CustomerEntity existing, CustomerRequestDto dto) {

        String newPan = dto.panNumber();
        String oldPan = existing.getPanNumber();

        if (newPan == null || newPan.equals(oldPan)) {
            return;
        }

        boolean exists = customerRepository
                .existsByPanNumberAndIdNot(newPan, existing.getId());

        if (exists) {
            throw new DuplicateResourceException(
                    "Customer",
                    "panNumber",
                    dto.panNumber()
            );
        }
    }


//    @Override
//    public boolean deleteCustomer(Long id) {
//        return customerRepository.findById(id)
//                .map(customer -> {
//                    customerRepository.delete(customer);
//                    return true;
//                }).orElse(false);
//    }

    @Transactional
    public boolean deleteCustomer(Long id, String performedBy, String reason){
        CustomerEntity customer = getCustomerOrThrow(id);

        if (customer.isDeleted()) {
            throw new IllegalStateException("Customer already deleted");
        }

       accountActionDispatcher.dispatch(
               AccountAction.DELETE,
               customer,
               reason,
               performedBy
       );

       customerRepository.save(customer);
       return true;
    }

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
