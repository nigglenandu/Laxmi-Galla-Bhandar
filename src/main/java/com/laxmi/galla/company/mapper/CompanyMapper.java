package com.laxmi.galla.company.mapper;

import com.laxmi.galla.transaction.dto.request.CompanyRequestDto;
import com.laxmi.galla.transaction.dto.response.CompanyResponseDto;
import com.laxmi.galla.transaction.domain.entity.Company;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    // Request DTO -> Entity
 // default is false
//    @Mapping(target = "transactions", ignore = true) // set manually if needed
    Company toCompanyEntity(CompanyRequestDto dto);

    // Entity -> Response DTO
    CompanyResponseDto toCompanyResponseDto(Company transaction);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCompany(CompanyRequestDto dto, @MappingTarget Company entity);
}
