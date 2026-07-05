package com.laxmi.galla.categories.application;

import com.laxmi.galla.categories.domain.entity.Category;
import com.laxmi.galla.categories.dto.request.CategoryRequestDto;
import com.laxmi.galla.categories.dto.response.CategoryResponseDto;
import com.laxmi.galla.categories.respository.CategoryRepository;
import com.laxmi.galla.core.exception.ResourceNotFoundException;
import com.laxmi.galla.core.pagination.PageResponse;
import com.laxmi.galla.core.pagination.PageResponseFactory;
import com.laxmi.galla.core.pagination.PaginationPolicy;
import com.laxmi.galla.core.security.context.AuthContext;
import com.laxmi.galla.mapper.CategoryMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository CategoryRepository;
    private final CategoryMapper CategoryMapper;
    private final PaginationPolicy paginationPolicy;
    private final AuthContext authContext;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto dto) {
        Category Category = CategoryMapper.toCategoryEntity(dto);
        Category saved = CategoryRepository.save(Category);
        return CategoryMapper.toCategoryResponseDto(saved);
    }


    @Override
    public PageResponse<CategoryResponseDto> getAllCategories(Pageable pageable) {
        Pageable safePageable = paginationPolicy.apply(pageable);

        Page<Category> page = CategoryRepository.findAll(safePageable);

        return PageResponseFactory.fromPage(page, CategoryMapper::toCategoryResponseDto);
    }

    @Override
    public CategoryResponseDto getCurrentCategoryProfile() {

        return CategoryMapper.toCategoryResponseDto(getCategoryOrThrow(authContext.getUserId()));
    }


    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto) {
        Category Category = getCategoryOrThrow(id);

        applyUpdate(Category, dto);

        Category updated = CategoryRepository.save(Category);

        return CategoryMapper.toCategoryResponseDto(updated);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateMyCategory(CategoryRequestDto dto) {

        Category Category = getCategoryOrThrow(authContext.getUserId());

        applyUpdate(Category, dto);

        Category updated = CategoryRepository.save(Category);

        String correlationId = Optional.ofNullable(authContext.getCorrelationId()).orElse(UUID.randomUUID().toString());

        return CategoryMapper.toCategoryResponseDto(updated);
    }

    private void applyUpdate(Category Category, CategoryRequestDto dto) {
        CategoryMapper.updateCategory(dto, Category);
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {

        Category Category = getCategoryOrThrow(id);

        Category.delete(authContext.getUserId().toString());

        CategoryRepository.save(Category);
    }

    @Transactional
    @Override
    public void restoreCategory(Long id) {

        Category Category = getCategoryOrThrowIncludingDeleted(id);

        Category.restoreCategory();

        CategoryRepository.save(Category);
    }

    private Category getCategoryOrThrow(Long id) {

        return CategoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", id.toString()));
    }


    private Category getCategoryOrThrowIncludingDeleted(Long id) {
        return CategoryRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", id.toString()));

    }
}