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
 * Bean验证工具类
 */
@Component
public class ValidationUtils {
    private final Validator validator;

    public ValidationUtils() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

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
     * 验证对象的所有约束
     *
     * @param obj 待验证对象
     * @throws IllegalArgumentException 如果验证失败，抛出此异常，包含所有验证错误信息
     */
    public <T> void validate(T obj) {
        String validationResult = validateObject(obj);
        if (validationResult != null) {
            throw new IllegalArgumentException("Validation failed: " + validationResult);
        }
    }

    public <T> void validateParams(T obj) {
        String validationResult = validateObject(obj);
        if (validationResult != null) {
            throw new ParamValidationException("Validation failed: " + validationResult).code(ErrorCode.PARAM_ERROR.getCode());
        }
    }

    public void validateLongFormat(String id) {
        try {
            Long.valueOf(id);
        } catch (NumberFormatException e) {
            throw new ParamValidationException("Validation failed: 参数必须为Long型，当前值为" + id).code(ErrorCode.PARAM_ERROR.getCode());
        }
    }
} 