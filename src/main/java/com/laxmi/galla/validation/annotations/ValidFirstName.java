package com.laxmi.galla.validation.annotations;

import com.laxmi.galla.validation.validators.FirstNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FirstNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFirstName {
    String message() default "{firstName.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
