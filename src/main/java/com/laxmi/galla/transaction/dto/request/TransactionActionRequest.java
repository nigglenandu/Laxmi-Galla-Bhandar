package com.laxmi.galla.transaction.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TransactionActionRequest(

        @NotBlank(message = "Reason is required")
        String reason

) {}