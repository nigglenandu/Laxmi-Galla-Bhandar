package com.laxmi.galla.services;

import com.laxmi.galla.dto.request.CategoryRequestDto;
import com.laxmi.galla.dto.response.CategoryResponseDto;
import com.laxmi.galla.entity.Category;
import com.laxmi.galla.mapper.CategoryMapper;
import com.laxmi.galla.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto dto) {
        Category category = categoryMapper.toCategoryEntity(dto);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toCategoryResponseDto(saved);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toCategoryResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CategoryResponseDto> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toCategoryResponseDto);
    }

    @Override
    public Optional<CategoryResponseDto> updateCategory(Long id, CategoryRequestDto dto) {
        return categoryRepository.findById(id)
                .map(existing -> {
                    existing.setName(dto.name());
                    Category updated = categoryRepository.save(existing);
                    return categoryMapper.toCategoryResponseDto(updated);
                });
    }

    @Override
    public boolean deleteCategory(Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    categoryRepository.delete(category);
                    return true;
                }).orElse(false);
    }

    @Override
    public Optional<Category> getCategoryEntityById(Long id) {
        return categoryRepository.findById(id);
    }
}