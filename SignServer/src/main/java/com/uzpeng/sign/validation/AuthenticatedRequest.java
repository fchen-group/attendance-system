package com.uzpeng.sign.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Constraint(validatedBy = AuthenticatedRequestValidator.class)
public @interface AuthenticatedRequest {
    String message() default "no authenticated!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
