package com.laxmi.galla.mapper;

import com.laxmi.galla.dto.*;
import com.laxmi.galla.dto.request.CustomerRequestDto;
import com.laxmi.galla.dto.response.CustomerResponseDto;
import com.laxmi.galla.entity.*;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // -----------------------------
    // Entity -> Response DTO
    // -----------------------------
//    @Mapping(target = "categories", source = "categories")
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
//    @Mapping(target = "isDeleted", ignore = true)
    CustomerResponseDto toCustomerResponseDto(CustomerEntity entity);

    // -----------------------------
    // Request DTO -> Entity
    // -----------------------------
//    @Mapping(target = "categories", ignore = true) // set manually from IDs in service
    CustomerEntity toCustomerEntity(CustomerRequestDto dto);

    // -----------------------------
    // Helper: Convert categories to IDs
    // -----------------------------
    default List<Long> mapCategoriesToIds(Set<Category> categories) {
        return categories.stream().map(Category::getId).toList();
    }
}
