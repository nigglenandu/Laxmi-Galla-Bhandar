package com.laxmi.galla.dto.request;

import com.laxmi.galla.enums.PurchaseOrSale;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequestDto(

        @NotNull(message = "{customer.required}")
        String customerId,

        @NotNull(message = "{company.required}")
        String companyId,

        @NotNull(message = "{amount.required}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{amount.invalid}")
        BigDecimal unitAmount,

        @NotNull(message = "{date.required}")
        LocalDateTime date,

        @Size(max = 255, message = "{description.size}")
        String description,

        @DecimalMin(value = "0.0", message = "{amount.invalid}")
        BigDecimal dueAmount,

        @NotNull(message = "{amount.required}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{amount.invalid}")
        BigDecimal totalAmount,

        @NotNull(message = "{quantity.required}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{quantity.invalid}")
        BigDecimal quantity,

        @NotNull(message = "{type.required}")
        PurchaseOrSale purchaseOrSale,

        String unitId
) {}