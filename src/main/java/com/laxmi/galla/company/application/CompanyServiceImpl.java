package com.laxmi.galla.company.application;

import com.laxmi.galla.company.dto.request.CompanyRequestDto;
import com.laxmi.galla.company.dto.request.CompanySearchCriteria;
import com.laxmi.galla.company.dto.response.CompanyResponseDto;
import com.laxmi.galla.company.domain.entity.Company;
import com.laxmi.galla.company.specification.CompanySpecification;
import com.laxmi.galla.core.exception.ResourceNotFoundException;
import com.laxmi.galla.core.pagination.PageResponse;
import com.laxmi.galla.core.pagination.PageResponseFactory;
import com.laxmi.galla.core.pagination.PaginationPolicy;
import com.laxmi.galla.core.security.context.AuthContext;
import com.laxmi.galla.Company.domain.entity.CompanyEntity;
import com.laxmi.galla.Company.domain.event.CompanyUpdatedEvent;
import com.laxmi.galla.customer.dto.request.CustomerRequestDto;
import com.laxmi.galla.customer.dto.response.CustomerResponseDto;
import com.laxmi.galla.entity.Category;
import com.laxmi.galla.mapper.CompanyMapper;
import com.laxmi.galla.company.respository.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements ICompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final PaginationPolicy paginationPolicy;
    private final AuthContext authContext;

    @Override
    public CompanyResponseDto createCompany(CompanyRequestDto dto) {
        Company company = companyMapper.toCompanyEntity(dto);
        Company saved = companyRepository.save(company);
        return companyMapper.toCompanyResponseDto(saved);
    }


    @Override
    public PageResponse<CompanyResponseDto> getAllCompanies(CompanySearchCriteria criteria, Pageable pageable) {
        Pageable safePageable = paginationPolicy.apply(pageable);

        Specification<Company> spec = CompanySpecification.withCriteria(criteria);

        Page<Company> page = companyRepository.findAll(spec, safePageable);

        return PageResponseFactory.fromPage(page, companyMapper::toCompanyResponseDto);
    }

    @Override
    public CompanyResponseDto getCurrentCompanyProfile() {

        return companyMapper.toCompanyResponseDto(getCompanyOrThrow(authContext.getUserId()));
    }


    @Transactional
    public CompanyResponseDto updateCompany(Long id, CompanyRequestDto dto) {
        Company company = getCompanyOrThrow(id);

        applyUpdate(company, dto);

        Company updated = companyRepository.save(company);

        String correlationId = Optional.ofNullable(authContext.getCorrelationId()).orElse(UUID.randomUUID().toString());

        eventPublisher.publishEvent(CompanyUpdatedEvent.of(updated.getId().toString(), correlationId));

        return companyMapper.toCompanyResponseDto(updated);
    }

    @Override
    @Transactional
    public CompanyResponseDto updateMyProfile(CompanyRequestDto dto) {

        CompanyEntity company = getCompanyOrThrow(authContext.getUserId());

        applyUpdate(company, dto);

        CompanyEntity updated = companyRepository.save(company);

        String correlationId = Optional.ofNullable(authContext.getCorrelationId()).orElse(UUID.randomUUID().toString());

        eventPublisher.publishEvent(CompanyUpdatedEven.of(updated.getId().toString(), correlationId));

        return companyMapper.toCompanyResponseDto(updated);
    }

    private void applyUpdate(CompanyEntity company, CompanyRequestDto dto) {
        validatePanUniqueness(company, dto);
        companyMapper.updateCompany(dto, company);
        if (dto.categoryIds() != null) {
            Set<Category> categories = fetchCategoryEntitiesByIds(dto.categoryIds());
            company.setCategories(categories);
        }
    }


    @Override
    public boolean deleteCustomer(Long id) {
        return companyRepository.findById(id)
                .map(company -> {
                    companyRepository.delete(company);
                    return true;
                }).orElse(false);
    }


    private Company getCompanyOrThrow(Long id) {

        return companyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Company", id.toString()));
    }

//    // Optional: Paginated response
//    public PaginatedResponse<CompanyResponseDto> getCompaniesPaginated(Pageable pageable) {
//        Page<Company> page = companyRepository.findAll(pageable);
//        List<CompanyResponseDto> content = page.getContent()
//                .stream()
//                .map(companyMapper::toCompanyResponseDto)
//                .collect(Collectors.toList());
//        return new PaginatedResponse<>(
//                content,
//                page.isLast(),
//                page.getNumber(),
//                page.getSize(),
//                page.getTotalElements(),
//                page.getTotalPages()
//        );
//    }
}
