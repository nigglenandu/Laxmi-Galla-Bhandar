package com.laxmi.galla.dto.response;

import java.util.Set;

public record CompanyResponseDto(
        Long id,
        String name,
        String panNumber,
        String phoneNo,
        String companyAddress,
        Set<TransactionInfoDTO> transactions
) {}