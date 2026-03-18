package com.laxmi.galla.validation.annotations;

import com.laxmi.galla.validation.validators.LastNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LastNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLastName {

    String message() default "{lastName.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}