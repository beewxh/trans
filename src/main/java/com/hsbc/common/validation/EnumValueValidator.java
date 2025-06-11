package com.hsbc.common.validation;

import com.hsbc.common.errorhandler.exception.FrameworkException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @ClassName EnumValueValidator
 * @Description TODO
 * @Author rd
 * @Date 2025/6/11 22:58
 * @Version 1.0
 **/

public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {
    private Class<? extends Enum<?>> enumClass;
    private String method;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.method = constraintAnnotation.method();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 允许 null，视需求调整
        }

        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            try {
                Object enumValue = enumConstant.getClass().getMethod(method).invoke(enumConstant);
                if (value.equals(enumValue) || value.toString().equals(enumValue.toString())) {
                    return true;
                }
            } catch (Exception e) {
                throw new FrameworkException("无法访问枚举属性", e);
            }
        }
        return false;
    }
}