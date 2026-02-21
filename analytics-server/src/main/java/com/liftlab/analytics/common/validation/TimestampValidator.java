package com.liftlab.analytics.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;

public class TimestampValidator implements ConstraintValidator<ValidTimestamp, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            // missing timestamp is allowed (ingest will use server time)
            return true;
        }
        try {
            Instant.parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
