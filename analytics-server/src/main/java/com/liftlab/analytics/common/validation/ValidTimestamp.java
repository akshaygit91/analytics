package com.liftlab.analytics.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = TimestampValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValidTimestamp {
    String message() default "timestamp must be ISO-8601 (e.g. 2024-03-15T14:30:00Z)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
