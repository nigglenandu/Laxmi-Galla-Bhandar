package com.laxmi.galla.validation.validators;

import com.laxmi.galla.validation.annotations.ValidPanNumber;
import com.laxmi.galla.validation.patterns.ValidationPatterns;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PanNumberValidator implements ConstraintValidator<ValidPanNumber, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // let @NotBlank handle required validation
        }

        String trimmed = value.trim();

        if (!ValidationPatterns.PAN_NUMBER.matcher(trimmed).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                context.getDefaultConstraintMessageTemplate() // message from annotation
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}