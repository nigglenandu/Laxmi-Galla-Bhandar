package com.laxmi.galla.validation.annotations;

import com.laxmi.galla.validation.validators.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "{password.invalid}";
    String lengthMessage() default "{password.length}"; // length-specific
    String patternMessage() default "{password.strength}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}