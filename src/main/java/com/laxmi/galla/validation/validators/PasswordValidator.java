package com.laxmi.galla.validation.validators;

import com.laxmi.galla.validation.annotations.ValidPassword;
import com.laxmi.galla.validation.patterns.ValidationPatterns;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate( context.getDefaultConstraintMessageTemplate())
                    .addConstraintViolation();
            return false;
        }

        String trimmed = value.trim();

        // Length check
        if (trimmed.length() < ValidationPatterns.PASSWORD_MIN_LENGTH
                || trimmed.length() > ValidationPatterns.PASSWORD_MAX_LENGTH) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            context.getDefaultConstraintMessageTemplate()
                    )
                    .addConstraintViolation();
            return false;
        }

        // Pattern check
        if (!ValidationPatterns.PASSWORD.matcher(trimmed).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            context.getDefaultConstraintMessageTemplate()
                    )
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}