package com.laxmi.galla.transaction.dto.request;

import com.laxmi.galla.domain.enums.PurchaseOrSale;

import java.time.LocalDate;

public record TransactionSearchCriteria(

        String searchTerm,

        Long companyId,

        Long customerId,

        Long unitId,

        PurchaseOrSale purchaseOrSale,

        LocalDate fromDate,

        LocalDate toDate,

        Boolean hasDueOnly
) {
}