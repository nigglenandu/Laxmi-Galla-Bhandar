package com.laxmi.galla.company.dto.response;

import com.laxmi.galla.dto.response.TransactionResponseDto;

import java.util.Set;

public record CompanyResponseDto(
        String id,
        String name,
        String panNumber,
        String phoneNo,
        String companyAddress,
        Set<TransactionResponseDto> transactions
) {}