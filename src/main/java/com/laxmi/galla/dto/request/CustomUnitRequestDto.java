package com.laxmi.galla.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record CustomUnitRequestDto(
    @NotBlank(message = "{unitName.required}")
    @Size(max = 100, message = "{unitName.size}")
    @Schema(description = "Name of the custom unit", example = "Kilogram", required = true)
    String unitName
) {}