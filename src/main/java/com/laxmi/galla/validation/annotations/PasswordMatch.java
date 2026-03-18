package com.laxmi.galla.validation.annotations;

import com.laxmi.galla.validation.validators.PasswordMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {

    String message() default "{password.match.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // dynamic field names
    String base();      // password field
    String confirm();   // confirm password field
}