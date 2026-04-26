package com.laxmi.galla.dto.request;

import com.laxmi.galla.validation.annotations.ValidNepaliPhone;
import com.laxmi.galla.validation.annotations.ValidPanNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyRequestDTO(

        @NotBlank(message = "{name.required}")
        @Size(max = 100, message = "{name.size}")
        String name,

        @ValidPanNumber
        String panNumber,

        @NotBlank(message = "{phoneNumber.required}")
        @ValidNepaliPhone
        String phoneNo,

        @Size(max = 255, message = "{address.size}")
        String companyAddress

) {}