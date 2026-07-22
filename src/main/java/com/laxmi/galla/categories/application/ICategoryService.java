package com.laxmi.galla.categories.application;

import com.laxmi.galla.categories.dto.request.CategoryRequestDto;
import com.laxmi.galla.categories.dto.response.CategoryResponseDto;
import com.laxmi.galla.core.pagination.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto dto);

    // Get all Categories as response DTOs
    PageResponse<CategoryResponseDto> getAllCategories(Pageable pageable);

    CategoryResponseDto getCurrentCategoryProfile();

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto);

    CategoryResponseDto updateMyCategory(CategoryRequestDto dto);

    void deleteCategory(Long id);

    void restoreCategory(Long id);
}
