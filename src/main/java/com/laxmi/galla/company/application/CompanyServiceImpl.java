package com.laxmi.galla.company.application;

import com.laxmi.galla.company.domain.event.CompanyDeletedEvent;
import com.laxmi.galla.company.domain.event.CompanyRestoredEvent;
import com.laxmi.galla.company.domain.event.CompanyUpdatedEvent;
import com.laxmi.galla.company.dto.request.CompanyActionRequest;
import com.laxmi.galla.company.dto.request.CompanyRequestDto;
import com.laxmi.galla.company.dto.request.CompanySearchCriteria;
import com.laxmi.galla.company.dto.response.CompanyResponseDto;
import com.laxmi.galla.company.domain.entity.Company;
import com.laxmi.galla.company.domain.specification.CompanySpecification;
import com.laxmi.galla.core.exception.DuplicateResourceException;
import com.laxmi.galla.core.exception.ResourceNotFoundException;
import com.laxmi.galla.core.pagination.PageResponse;
import com.laxmi.galla.core.pagination.PageResponseFactory;
import com.laxmi.galla.core.pagination.PaginationPolicy;
import com.laxmi.galla.core.security.context.AuthContext;
import com.laxmi.galla.company.mapper.CompanyMapper;
import com.laxmi.galla.company.respository.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements ICompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final PaginationPolicy paginationPolicy;
    private final AuthContext authContext;
    private final ApplicationEventPublisher eventPublisher;

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
    public CompanyResponseDto updateMyCompany(CompanyRequestDto dto) {

        Company company = getCompanyOrThrow(authContext.getUserId());

        applyUpdate(company, dto);

        Company updated = companyRepository.save(company);

        String correlationId = Optional.ofNullable(authContext.getCorrelationId()).orElse(UUID.randomUUID().toString());

        eventPublisher.publishEvent(CompanyUpdatedEvent.of(updated.getId().toString(), correlationId));

        return companyMapper.toCompanyResponseDto(updated);
    }

    private void applyUpdate(Company company, CompanyRequestDto dto) {
        validatePanUniqueness(company, dto);
        companyMapper.updateCompany(dto, company);
    }

    @Transactional
    @Override
    public void deleteCompany(Long id, CompanyActionRequest request) {

        Company company = getCompanyOrThrow(id);

        company.delete(authContext.getUserId().toString());

        companyRepository.save(company);

        eventPublisher.publishEvent(
                CompanyDeletedEvent.of(
                        company.getId().toString(),
                        authContext.getEmail(),
                        request.reason(),
                        authContext.getCorrelationId()
                )
        );
    }

    @Transactional
    @Override
    public void restoreCompany(Long id, CompanyActionRequest request) {

        Company company = getCompanyOrThrowIncludingDeleted(id);

        company.restoreCompany();

        companyRepository.save(company);

        eventPublisher.publishEvent(
                CompanyRestoredEvent.of(
                        company.getId().toString(),
                        authContext.getEmail(),
                        request.reason(),
                        authContext.getCorrelationId()
                )
        );
    }

    private Company getCompanyOrThrow(Long id) {

        return companyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Company", id.toString()));
    }

    private void validatePanUniqueness(Company existing, CompanyRequestDto dto) {

        String newPan = dto.panNumber();
        String oldPan = existing.getPanNumber();

        if (newPan == null || newPan.equals(oldPan)) {
            return;
        }

        boolean exists = companyRepository.existsByPanNumberAndIdNot(newPan, existing.getId());

        if (exists) {
            throw new DuplicateResourceException("Company", "panNumber", dto.panNumber());
        }
    }

    private Company getCompanyOrThrowIncludingDeleted(Long id) {
        return companyRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Company", id.toString()));

    }
}