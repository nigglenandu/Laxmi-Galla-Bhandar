package com.laxmi.galla.validation.validators;

import com.laxmi.galla.validation.annotations.ValidNepaliPhone;
import com.laxmi.galla.validation.patterns.ValidationPatterns;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class NepaliPhoneValidator implements ConstraintValidator<ValidNepaliPhone, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null/blank values are valid by default
        if (value == null || value.isBlank()) {
            return true; // let @NotBlank handle required validation if needed
        }

        String trimmed = value.trim();

        // Pattern check
        if (!ValidationPatterns.NEPALI_PHONE.matcher(trimmed).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate() // resolves message from annotation
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}