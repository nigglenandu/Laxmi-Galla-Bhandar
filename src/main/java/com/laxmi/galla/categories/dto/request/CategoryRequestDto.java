package com.laxmi.galla.categories.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryRequestDto(

        @NotBlank(message = "{name.required}")
        @Size(max = 100, message = "{name.size}")
        @Schema(description = "Name of the category", example = "Mill", required = true)
        String name

) {}