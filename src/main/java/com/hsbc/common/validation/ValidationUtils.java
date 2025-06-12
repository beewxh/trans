package com.hsbc.common.validation;

import com.hsbc.common.errorhandler.exception.ParamValidationException;
import com.hsbc.trans.enums.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Parameter Validation Utility Class
 * Provides parameter validation functionality based on JSR-380 (Bean Validation 2.0)
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Component
public class ValidationUtils {
    private final Validator validator;

    /**
     * Constructor, initializes the validator
     */
    public ValidationUtils() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    /**
     * Validate object and return validation result
     *
     * @param obj Object to validate
     * @return Error message if validation fails
     * @param <T> Object type
     */
    private <T> String validateObject(T obj) {
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        if (violations.isEmpty()) {
            return null;
        }
        return violations.stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));
    }

    /**
     * Validate object, throw IllegalArgumentException if validation fails
     *
     * @param obj Object to validate
     * @param <T> Object type
     * @throws IllegalArgumentException Thrown when validation fails
     */
    public <T> void validate(T obj) {
        String validationResult = validateObject(obj);
        if (validationResult != null) {
            throw new IllegalArgumentException("Parameter validation failed: " + validationResult);
        }
    }

    /**
     * Validate object, throw ParamValidationException if validation fails
     *
     * @param obj Object to validate
     * @param <T> Object type
     * @throws ParamValidationException Thrown when validation fails
     */
    public <T> void validateParams(T obj) {
        String validationResult = validateObject(obj);
        if (validationResult != null) {
            throw new ParamValidationException("Parameter validation failed: " + validationResult).code(ErrorCode.PARAM_ERROR.getCode());
        }
    }

    /**
     * Validate string is a valid Long type
     *
     * @param id String to validate
     * @throws ParamValidationException Thrown when validation fails
     */
    public void validateLongFormat(String id) {
        try {
            Long.valueOf(id);
        } catch (NumberFormatException e) {
            throw new ParamValidationException("Parameter validation failed: Parameter must be Long type, current value is " + id).code(ErrorCode.PARAM_ERROR.getCode());
        }
    }
} 