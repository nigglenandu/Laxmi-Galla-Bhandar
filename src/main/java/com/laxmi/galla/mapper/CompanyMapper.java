package com.laxmi.galla.mapper;

import com.laxmi.galla.dto.request.CompanyRequestDto;
import com.laxmi.galla.dto.response.CompanyResponseDto;
import com.laxmi.galla.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    // Request DTO -> Entity
 // default is false
//    @Mapping(target = "transactions", ignore = true) // set manually if needed
    Company toCompanyEntity(CompanyRequestDto dto);

    // Entity -> Response DTO
    CompanyResponseDto toCompanyResponseDto(Company company);
}
