package com.laxmi.galla.dto.response;

import com.laxmi.galla.enums.PurchaseOrSale;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDto(

        String id,

        String customerName,
        String companyName,

        BigDecimal unitAmount,
        BigDecimal totalAmount,
        BigDecimal dueAmount,
        BigDecimal quantity,

        LocalDateTime date,
        String description,

        PurchaseOrSale purchaseOrSale,

        String unitName
) {}