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
 * 参数验证工具类
 * 提供基于JSR-380(Bean Validation 2.0)的参数验证功能
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Component
public class ValidationUtils {
    private final Validator validator;

    /**
     * 构造函数，初始化验证器
     */
    public ValidationUtils() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    /**
     * 验证对象并返回验证结果
     *
     * @param obj 待验证的对象
     * @return 验证失败时返回错误信息，验证成功时返回null
     * @param <T> 对象类型
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
     * 验证对象，验证失败时抛出IllegalArgumentException
     *
     * @param obj 待验证的对象
     * @param <T> 对象类型
     * @throws IllegalArgumentException 当验证失败时抛出此异常
     */
    public <T> void validate(T obj) {
        String validationResult = validateObject(obj);
        if (validationResult != null) {
            throw new IllegalArgumentException("参数验证失败: " + validationResult);
        }
    }

    /**
     * 验证对象，验证失败时抛出ParamValidationException
     *
     * @param obj 待验证的对象
     * @param <T> 对象类型
     * @throws ParamValidationException 当验证失败时抛出此异常
     */
    public <T> void validateParams(T obj) {
        String validationResult = validateObject(obj);
        if (validationResult != null) {
            throw new ParamValidationException("参数验证失败: " + validationResult).code(ErrorCode.PARAM_ERROR.getCode());
        }
    }

    /**
     * 验证字符串是否为有效的Long类型
     *
     * @param id 待验证的字符串
     * @throws ParamValidationException 当验证失败时抛出此异常
     */
    public void validateLongFormat(String id) {
        try {
            Long.valueOf(id);
        } catch (NumberFormatException e) {
            throw new ParamValidationException("参数验证失败: 参数必须为Long型，当前值为" + id).code(ErrorCode.PARAM_ERROR.getCode());
        }
    }
} 