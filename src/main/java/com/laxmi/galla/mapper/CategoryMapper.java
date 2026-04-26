package com.laxmi.galla.mapper;

import com.laxmi.galla.dto.request.CategoryRequestDto;
import com.laxmi.galla.dto.response.CategoryResponseDto;
import com.laxmi.galla.entity.*;
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
}