package com.laxmi.galla.mapper;

import com.laxmi.galla.transaction.domain.entity.Transaction;
import com.laxmi.galla.transaction.dto.request.TransactionRequestDto;
import com.laxmi.galla.transaction.dto.response.TransactionResponseDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    // Request DTO -> Entity
 // default is false
//    @Mapping(target = "transactions", ignore = true) // set manually if needed
    Transaction toTransactionEntity(TransactionRequestDto dto);

    // Entity -> Response DTO
    TransactionResponseDto toTransactionResponseDto(Transaction transaction);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTransaction(TransactionRequestDto dto, @MappingTarget Transaction entity);
}
