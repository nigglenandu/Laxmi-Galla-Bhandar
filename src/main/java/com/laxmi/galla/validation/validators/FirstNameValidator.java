package com.laxmi.galla.validation.validators;

import com.laxmi.galla.validation.annotations.ValidFirstName;
import com.laxmi.galla.validation.patterns.ValidationPatterns;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class FirstNameValidator implements ConstraintValidator<ValidFirstName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // null/blank handled by @NotBlank if needed
        }

        String trimmed = value.trim();

        if (!ValidationPatterns.FIRST_NAME.matcher(trimmed).matches()) {
            // Build a custom, clear error message
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Each part of the first name must start with an uppercase letter and can include letters, spaces, hyphens, and apostrophes"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}