package com.laxmi.galla.validation.annotations;

import com.laxmi.galla.validation.validators.PanNumberValidator;
import com.laxmi.galla.validation.validators.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PanNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPanNumber {

    String message() default "{pan.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}