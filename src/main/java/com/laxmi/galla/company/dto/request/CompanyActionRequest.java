package com.laxmi.galla.company.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CompanyActionRequest(

        @NotBlank(message = "Reason is required")
        String reason

) {}