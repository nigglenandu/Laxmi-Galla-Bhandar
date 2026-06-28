package com.laxmi.galla.customer.dto.request;

import com.laxmi.galla.validation.annotations.ValidFirstName;
import com.laxmi.galla.validation.annotations.ValidLastName;
import com.laxmi.galla.validation.annotations.ValidPanNumber;
import jakarta.validation.constraints.*;

import java.util.Set;

public record CustomerRequestDto(

        @NotBlank(message = "{firstName.required}")
        @Size(max = 80, message = "{firstName.size}")
        @ValidFirstName
        String firstName,

        @NotBlank(message = "{lastName.required}")
        @Size(max = 80, message = "{lastName.size}")
        @ValidLastName
        String lastName,

        @Size(max = 255, message = "{address.size}")
        String address,

        @ValidPanNumber
        String panNumber,

        Set<Long> categoryIds  // Just send category IDs; you can map them in service layer
) {
}