package com.laxmi.galla.mapper;

import com.laxmi.galla.categories.domain.entity.Category;
import com.laxmi.galla.categories.dto.request.CategoryRequestDto;
import com.laxmi.galla.categories.dto.response.CategoryResponseDto;
import com.laxmi.galla.company.domain.entity.Company;
import com.laxmi.galla.company.dto.request.CompanyRequestDto;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Entity -> Response DTO
    CategoryResponseDto toCategoryResponseDto(Category category);

    // Request DTO -> Entity
    Category toCategoryEntity(CategoryRequestDto dto);

    // Helper: convert set to list of IDs (if used in another mapper)
    default List<Long> mapCategoriesToIds(Set<Category> categories) {
        return categories.stream().map(Category::getId).toList();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategory(CategoryRequestDto dto, @MappingTarget Category entity);
}