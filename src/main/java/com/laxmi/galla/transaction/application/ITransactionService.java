package com.laxmi.galla.transaction.application;

import com.laxmi.galla.transaction.dto.request.TransactionActionRequest;
import com.laxmi.galla.transaction.dto.request.TransactionRequestDto;
import com.laxmi.galla.transaction.dto.request.TransactionSearchCriteria;
import com.laxmi.galla.transaction.dto.response.TransactionResponseDto;
import com.laxmi.galla.core.pagination.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ITransactionService {
    TransactionResponseDto createTransaction(TransactionRequestDto dto);

    // Get all companies as response DTOs
    PageResponse<TransactionResponseDto> getAllCompanies(TransactionSearchCriteria criteria,
                                                     Pageable pageable);

    TransactionResponseDto getCurrentTransactionProfile();

    TransactionResponseDto updateTransaction(Long id, TransactionRequestDto dto);

    TransactionResponseDto updateMyTransaction(TransactionRequestDto dto);

    void deleteTransaction(Long id, TransactionActionRequest request);

    void restoreTransaction(Long id, TransactionActionRequest request);

//    PaginatedResponse<TransactionResponseDto> getCompaniesPaginated(Pageable pageable);
}
