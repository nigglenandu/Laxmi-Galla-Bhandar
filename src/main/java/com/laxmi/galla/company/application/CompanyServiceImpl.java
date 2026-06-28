package com.laxmi.galla.services;

import com.laxmi.galla.dto.request.CompanyRequestDto;
import com.laxmi.galla.company.dto.response.CompanyResponseDto;
import com.laxmi.galla.company.domain.entity.Company;
import com.laxmi.galla.mapper.CompanyMapper;
import com.laxmi.galla.company.respository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements ICompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    @Override
    public CompanyResponseDto createCustomer(CompanyRequestDto dto) {
        Company company = companyMapper.toCompanyEntity(dto);
        Company saved = companyRepository.save(company);
        return companyMapper.toCompanyResponseDto(saved);
    }

    @Override
    public List<CompanyResponseDto> getAllCustomers() {
        return companyRepository.findAll()
                .stream()
                .map(companyMapper::toCompanyResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CompanyResponseDto> getCustomerById(Long id) {
        return companyRepository.findById(id)
                .map(companyMapper::toCompanyResponseDto);
    }

    @Override
    public Optional<CompanyResponseDto> updateCustomer(Long id, CompanyRequestDto dto) {
        return companyRepository.findById(id)
                .map(existing -> {
                    existing.setName(dto.name());
                    existing.setPanNo(dto.panNo());
                    existing.setCompanyAddress(dto.companyAddress());
                    Company updated = companyRepository.save(existing);
                    return companyMapper.toCompanyResponseDto(updated);
                });
    }

    @Override
    public boolean deleteCustomer(Long id) {
        return companyRepository.findById(id)
                .map(company -> {
                    companyRepository.delete(company);
                    return true;
                }).orElse(false);
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
