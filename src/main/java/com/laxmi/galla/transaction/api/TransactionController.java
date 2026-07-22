package com.laxmi.galla.transaction.api;

import com.laxmi.galla.transaction.application.ITransactionService;
import com.laxmi.galla.transaction.dto.request.TransactionActionRequest;
import com.laxmi.galla.transaction.dto.request.TransactionRequestDto;
import com.laxmi.galla.transaction.dto.request.TransactionSearchCriteria;
import com.laxmi.galla.transaction.dto.response.TransactionResponseDto;
import com.laxmi.galla.core.dto.response.ApiResult;
import com.laxmi.galla.core.pagination.PageResponse;
import com.laxmi.galla.core.pagination.PageResponseAssembler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {

    private final ITransactionService transactionService;

    @PostMapping
    public ApiResult<TransactionResponseDto> createTransaction(@RequestBody TransactionRequestDto dto) {
        TransactionResponseDto response = transactionService.createTransaction(dto);
        return ApiResult.created(response).toBuilder().message("Transaction creation completed").build();
    }

    @GetMapping
    public ApiResult<PageResponse<TransactionResponseDto>> getAllCompanies(
            @ParameterObject Pageable pageable,
            @ModelAttribute TransactionSearchCriteria criteria
            ) {
        PageResponse<TransactionResponseDto> pageResponse = transactionService.getAllCompanies(criteria, pageable);
        PageResponse<TransactionResponseDto> enriched = PageResponseAssembler.of(pageResponse)
                .withLinks("/api/v1/companies")
                .withMetadata("criteria", criteria)
                .assemble();
        return ApiResult.ok(enriched);
    }

    @GetMapping()
    public ApiResult<TransactionResponseDto> getMyProfile() {
       return ApiResult.ok(transactionService.getCurrentTransactionProfile());
    }

    @PutMapping("/{id}")
    public ApiResult<TransactionResponseDto> updateTransaction(@PathVariable Long id,
                                                            @Valid @RequestBody TransactionRequestDto dto) {
       TransactionResponseDto response = transactionService.updateTransaction(id, dto);
       return ApiResult.ok(response)
               .toBuilder()
               .message("Transaction updated successfully")
               .build();
    }

    @PutMapping("/me")
    public ApiResult<TransactionResponseDto> updateMyTransaction(@Valid @RequestBody TransactionRequestDto dto) {
        TransactionResponseDto response = transactionService.updateMyTransaction(dto);
        return ApiResult.ok(response).toBuilder().message("Transaction updated successfully").build();
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionActionRequest request) {

        transactionService.deleteTransaction(id, request);

        return ApiResult.noContent()
                .toBuilder()
                .message("Transaction deleted successfully")
                .build();
    }

    @PatchMapping("/{id}/restore")
    public ApiResult<Void> restoreTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionActionRequest request) {

        transactionService.restoreTransaction(id, request);

        return ApiResult.<Void>ok(null)
                .toBuilder()
                .message("Transaction restored successfully")
                .build();
    }

    // Optional: Pagination endpoint
//    @GetMapping
//    public ResponseEntity<PaginatedResponse<TransactionResponseDto>> getCompaniesPaginated(Pageable pageable) {
//        return ResponseEntity.ok(transactionService.getCompaniesPaginated(pageable));
//    }
}
