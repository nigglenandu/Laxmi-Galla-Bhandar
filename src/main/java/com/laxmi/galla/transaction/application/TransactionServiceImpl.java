package com.laxmi.galla.transaction.application;

import com.laxmi.galla.transaction.domain.entity.Transaction;
import com.laxmi.galla.transaction.domain.event.TransactionDeletedEvent;
import com.laxmi.galla.transaction.domain.event.TransactionRestoredEvent;
import com.laxmi.galla.transaction.domain.event.TransactionUpdatedEvent;
import com.laxmi.galla.transaction.domain.specification.TransactionSpecification;
import com.laxmi.galla.transaction.dto.request.TransactionActionRequest;
import com.laxmi.galla.transaction.dto.request.TransactionRequestDto;
import com.laxmi.galla.transaction.dto.request.TransactionSearchCriteria;
import com.laxmi.galla.transaction.dto.response.TransactionResponseDto;
import com.laxmi.galla.transaction.repository.TransactionRepository;
import com.laxmi.galla.core.exception.ResourceNotFoundException;
import com.laxmi.galla.core.pagination.PageResponse;
import com.laxmi.galla.core.pagination.PageResponseFactory;
import com.laxmi.galla.core.pagination.PaginationPolicy;
import com.laxmi.galla.core.security.context.AuthContext;
import com.laxmi.galla.transaction.mapper.TransactionMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final PaginationPolicy paginationPolicy;
    private final AuthContext authContext;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public TransactionResponseDto createTransaction(TransactionRequestDto dto) {
        Transaction transaction = transactionMapper.toTransactionEntity(dto);
        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toTransactionResponseDto(saved);
    }


    @Override
    public PageResponse<TransactionResponseDto> getAllCompanies(TransactionSearchCriteria criteria, Pageable pageable) {
        Pageable safePageable = paginationPolicy.apply(pageable);

        Specification<Transaction> spec = TransactionSpecification.withCriteria(criteria);

        Page<Transaction> page = transactionRepository.findAll(spec, safePageable);

        return PageResponseFactory.fromPage(page, transactionMapper::toTransactionResponseDto);
    }

    @Override
    public TransactionResponseDto getCurrentTransactionProfile() {

        return transactionMapper.toTransactionResponseDto(getTransactionOrThrow(authContext.getUserId()));
    }


    @Transactional
    public TransactionResponseDto updateTransaction(Long id, TransactionRequestDto dto) {
        Transaction transaction = getTransactionOrThrow(id);

        applyUpdate(transaction, dto);

        Transaction updated = transactionRepository.save(transaction);

        String correlationId = Optional.ofNullable(authContext.getCorrelationId()).orElse(UUID.randomUUID().toString());

        eventPublisher.publishEvent(TransactionUpdatedEvent.of(updated.getId().toString(), correlationId));

        return transactionMapper.toTransactionResponseDto(updated);
    }

    @Override
    @Transactional
    public TransactionResponseDto updateMyTransaction(TransactionRequestDto dto) {

        Transaction transaction = getTransactionOrThrow(authContext.getUserId());

        applyUpdate(transaction, dto);

        Transaction updated = transactionRepository.save(transaction);

        String correlationId = Optional.ofNullable(authContext.getCorrelationId()).orElse(UUID.randomUUID().toString());

        eventPublisher.publishEvent(TransactionUpdatedEvent.of(updated.getId().toString(), correlationId));

        return transactionMapper.toTransactionResponseDto(updated);
    }

    private void applyUpdate(Transaction transaction, TransactionRequestDto dto) {
        transactionMapper.updateTransaction(dto, transaction);
    }

    @Transactional
    @Override
    public void deleteTransaction(Long id, TransactionActionRequest request) {

        Transaction transaction = getTransactionOrThrow(id);

        transaction.delete(authContext.getUserId().toString());

        transactionRepository.save(transaction);

        eventPublisher.publishEvent(
                TransactionDeletedEvent.of(
                        transaction.getId().toString(),
                        authContext.getEmail(),
                        request.reason(),
                        authContext.getCorrelationId()
                )
        );
    }

    @Transactional
    @Override
    public void restoreTransaction(Long id, TransactionActionRequest request) {

        Transaction transaction = getTransactionOrThrowIncludingDeleted(id);

        transaction.restoreTransaction();

        transactionRepository.save(transaction);

        eventPublisher.publishEvent(
                TransactionRestoredEvent.of(
                        transaction.getId().toString(),
                        authContext.getEmail(),
                        request.reason(),
                        authContext.getCorrelationId()
                )
        );
    }

    private Transaction getTransactionOrThrow(Long id) {

        return transactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction", id.toString()));
    }


    private Transaction getTransactionOrThrowIncludingDeleted(Long id) {
        return transactionRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction", id.toString()));

    }
}