package com.hsbc.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Enum Value Validation Annotation
 * Used to validate if a field value is a valid value in the specified enum class
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValueValidator.class)
@Documented
public @interface EnumValue {
    /**
     * Error message when validation fails
     */
    String message() default "Must be a valid enum value";

    /**
     * Validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Validation payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Enum class to validate against
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * Method name to get enum value, defaults to name() method
     */
    String method() default "name";
}