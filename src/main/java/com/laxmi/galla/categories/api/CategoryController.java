package com.laxmi.galla.categories.api;

import com.laxmi.galla.categories.application.ICategoryService;
import com.laxmi.galla.categories.dto.request.CategoryRequestDto;
import com.laxmi.galla.categories.dto.response.CategoryResponseDto;
import com.laxmi.galla.core.dto.response.ApiResult;
import com.laxmi.galla.core.pagination.PageResponse;
import com.laxmi.galla.core.pagination.PageResponseAssembler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Category")
public class CategoryController {

    private final ICategoryService CategoryService;

    @PostMapping
    public ApiResult<CategoryResponseDto> createCategory(@RequestBody CategoryRequestDto dto) {
        CategoryResponseDto response = CategoryService.createCategory(dto);
        return ApiResult.created(response).toBuilder().message("Category creation completed").build();
    }

    @GetMapping
    public ApiResult<PageResponse<CategoryResponseDto>> getAllCategories(
            @ParameterObject Pageable pageable
            ) {
        PageResponse<CategoryResponseDto> pageResponse = CategoryService.getAllCategories(pageable);
        PageResponse<CategoryResponseDto> enriched = PageResponseAssembler.of(pageResponse)
                .withLinks("/api/v1/Categories")
                .assemble();
        return ApiResult.ok(enriched);
    }

    @GetMapping()
    public ApiResult<CategoryResponseDto> getMyProfile() {
       return ApiResult.ok(CategoryService.getCurrentCategoryProfile());
    }

    @PutMapping("/{id}")
    public ApiResult<CategoryResponseDto> updateCategory(@PathVariable Long id,
                                                       @Valid @RequestBody CategoryRequestDto dto) {
       CategoryResponseDto response = CategoryService.updateCategory(id, dto);
       return ApiResult.ok(response)
               .toBuilder()
               .message("Category updated successfully")
               .build();
    }

    @PutMapping("/me")
    public ApiResult<CategoryResponseDto> updateMyCategory(@Valid @RequestBody CategoryRequestDto dto) {
        CategoryResponseDto response = CategoryService.updateMyCategory(dto);
        return ApiResult.ok(response).toBuilder().message("Category updated successfully").build();
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteCategory(
            @PathVariable Long id) {

        CategoryService.deleteCategory(id);

        return ApiResult.noContent()
                .toBuilder()
                .message("Category deleted successfully")
                .build();
    }

    @PatchMapping("/{id}/restore")
    public ApiResult<Void> restoreCategory(
            @PathVariable Long id) {

        CategoryService.restoreCategory(id);

        return ApiResult.<Void>ok(null)
                .toBuilder()
                .message("Category restored successfully")
                .build();
    }

    // Optional: Pagination endpoint
//    @GetMapping
//    public ResponseEntity<PaginatedResponse<CategoryResponseDto>> getCategoriesPaginated(Pageable pageable) {
//        return ResponseEntity.ok(CategoryService.getCategoriesPaginated(pageable));
//    }
}
