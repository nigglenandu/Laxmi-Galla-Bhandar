package com.laxmi.galla.validation.validators;

import com.laxmi.galla.validation.annotations.PasswordMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.Objects;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String baseFieldName;
    private String confirmFieldName;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        this.baseFieldName = constraintAnnotation.base();
        this.confirmFieldName = constraintAnnotation.confirm();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        try {
            Field baseField = value.getClass().getDeclaredField(baseFieldName);
            Field confirmField = value.getClass().getDeclaredField(confirmFieldName);

            baseField.setAccessible(true);
            confirmField.setAccessible(true);

            String baseValue = (String) baseField.get(value);
            String confirmValue = (String) confirmField.get(value);

            if (baseValue == null || confirmValue == null) return true;

            boolean valid = Objects.equals(baseValue, confirmValue);
            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode(confirmFieldName)
                        .addConstraintViolation();
            }

            return valid;

        } catch (NoSuchFieldException | IllegalAccessException e) {
            // fail safe → invalid if fields are not found
            return false;
        }
    }
}