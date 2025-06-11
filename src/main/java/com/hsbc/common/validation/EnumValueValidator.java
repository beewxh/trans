package com.hsbc.common.validation;

import com.hsbc.common.errorhandler.exception.FrameworkException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 枚举值验证器
 * 实现对{@link EnumValue}注解的验证逻辑，用于验证字段值是否为指定枚举类中的有效值
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {
    private Class<? extends Enum<?>> enumClass;
    private String method;

    /**
     * 初始化验证器
     *
     * @param constraintAnnotation 枚举值验证注解
     */
    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.method = constraintAnnotation.method();
    }

    /**
     * 执行验证
     * 通过反射调用指定的方法获取枚举值，并与待验证的值进行比较
     *
     * @param value 待验证的值
     * @param context 验证上下文
     * @return 验证是否通过
     * @throws FrameworkException 当无法访问枚举属性时抛出此异常
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
                throw new FrameworkException("无法访问枚举属性", e);
            }
        }
        return false;
    }
}