package com.hsbc.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 枚举值验证注解
 * 用于验证字段值是否为指定枚举类中的有效值
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
     * 验证失败时的错误消息
     */
    String message() default "必须是有效的枚举值";

    /**
     * 验证分组
     */
    Class<?>[] groups() default {};

    /**
     * 验证载荷
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 要验证的枚举类
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * 用于获取枚举值的方法名，默认使用name()方法
     */
    String method() default "name";
}