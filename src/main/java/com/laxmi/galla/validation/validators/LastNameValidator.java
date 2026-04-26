package com.laxmi.galla.validation.validators;

import com.laxmi.galla.validation.annotations.ValidLastName;
import com.laxmi.galla.validation.patterns.ValidationPatterns;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LastNameValidator implements ConstraintValidator<ValidLastName, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            return true; // handled by @NotBlank if required
        }

        String trimmed = value.trim();

        if (!ValidationPatterns.LAST_NAME.matcher(trimmed).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate()
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
