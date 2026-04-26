package com.laxmi.galla.services;

import com.laxmi.galla.dto.request.CategoryRequestDto;
import com.laxmi.galla.dto.response.CategoryResponseDto;
import com.laxmi.galla.entity.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {

    CategoryResponseDto createCategory(CategoryRequestDto dto);

    List<CategoryResponseDto> getAllCategories();

    Optional<CategoryResponseDto> getCategoryById(Long id);

    Optional<CategoryResponseDto> updateCategory(Long id, CategoryRequestDto dto);

    boolean deleteCategory(Long id);

    Optional<Category> getCategoryEntityById(Long id);
}
