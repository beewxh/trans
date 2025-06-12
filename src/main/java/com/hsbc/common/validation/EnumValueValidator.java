package com.hsbc.common.validation;

import com.hsbc.common.errorhandler.exception.FrameworkException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Enum Value Validator
 * Implements validation logic for {@link EnumValue} annotation, used to validate if a field value is a valid value in the specified enum class
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {
    private Class<? extends Enum<?>> enumClass;
    private String method;

    /**
     * Initialize validator
     *
     * @param constraintAnnotation Enum value validation annotation
     */
    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.method = constraintAnnotation.method();
    }

    /**
     * Perform validation
     * Uses reflection to call the specified method to get enum value and compare with the value to validate
     *
     * @param value Value to validate
     * @param context Validation context
     * @return Whether validation passed
     * @throws FrameworkException When enum property cannot be accessed
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 允许null值，如果不允许null应该配合@NotNull使用
        }

        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            try {
                Object enumValue = enumConstant.getClass().getMethod(method).invoke(enumConstant);
                if (value.equals(enumValue) || value.toString().equals(enumValue.toString())) {
                    return true;
                }
            } catch (Exception e) {
                throw new FrameworkException("Cannot access enum property", e);
            }
        }
        return false;
    }
}