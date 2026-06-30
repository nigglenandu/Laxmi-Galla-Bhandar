package com.laxmi.galla.mapper;

import com.laxmi.galla.company.dto.request.CompanyRequestDto;
import com.laxmi.galla.company.dto.response.CompanyResponseDto;
import com.laxmi.galla.company.domain.entity.Company;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    // Request DTO -> Entity
 // default is false
//    @Mapping(target = "transactions", ignore = true) // set manually if needed
    Company toCompanyEntity(CompanyRequestDto dto);

    // Entity -> Response DTO
    CompanyResponseDto toCompanyResponseDto(Company company);
}
