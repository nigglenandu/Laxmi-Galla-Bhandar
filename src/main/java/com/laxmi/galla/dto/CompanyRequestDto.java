package com.laxmi.galla.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyRequestDto(
        @NotBlank(message = "Company name cannot be blank")
        @Size(max = 255, message = "Company name is too long")
        String name,

        @Size(max = 50)
        String panNo,

        @Size(max = 500)
        String companyAddress
) {}
