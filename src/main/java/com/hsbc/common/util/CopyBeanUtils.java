package com.hsbc.common.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Bean属性拷贝工具类
 */
@Slf4j
public class CopyBeanUtils {
    // 缓存类的字段信息，提高性能
    private static final Map<Class<?>, List<Field>> FIELDS_CACHE = new ConcurrentHashMap<>();

    /**
     * 将source对象的属性拷贝到destination对象
     *
     * @param source 源对象
     * @param destination 目标对象
     */
    public static void copyProperties(Object source, Object destination) {
        if (source == null || destination == null) {
            return;
        }

        List<Field> sourceFields = getFields(source.getClass());
        List<Field> destFields = getFields(destination.getClass());

        // 遍历目标对象的字段
        for (Field destField : destFields) {
            // 在源对象中查找同名同类型的字段
            sourceFields.stream()
                .filter(sourceField -> isFieldMatch(sourceField, destField))
                .findFirst()
                .ifPresent(sourceField -> copyFieldValue(source, destination, sourceField, destField));
        }
    }

    /**
     * 获取类的所有字段（包括父类字段），并缓存结果
     */
    private static List<Field> getFields(Class<?> clazz) {
        return FIELDS_CACHE.computeIfAbsent(clazz, k -> {
            List<Field> fields = Arrays.stream(k.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .collect(Collectors.toList());

            // 获取父类的字段
            Class<?> superClass = k.getSuperclass();
            while (superClass != null && superClass != Object.class) {
                fields.addAll(Arrays.stream(superClass.getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .filter(field -> !Modifier.isFinal(field.getModifiers()))
                    .collect(Collectors.toList()));
                superClass = superClass.getSuperclass();
            }

            return fields;
        });
    }

    /**
     * 判断两个字段是否匹配（名称和类型都相同）
     */
    private static boolean isFieldMatch(Field sourceField, Field destField) {
        return sourceField.getName().equals(destField.getName()) &&
            sourceField.getType().equals(destField.getType());
    }

    /**
     * 复制字段值
     */
    private static void copyFieldValue(Object source, Object destination, Field sourceField, Field destField) {
        try {
            sourceField.setAccessible(true);
            destField.setAccessible(true);
            Object value = sourceField.get(source);
            destField.set(destination, value);
        } catch (Exception e) {
            log.warn("Failed to copy field value: {} -> {}", sourceField.getName(), destField.getName(), e);
        }
    }

    /**
     * 创建目标类的新实例并复制属性
     *
     * @param source 源对象
     * @param targetClass 目标类
     * @return 复制属性后的新实例
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            copyProperties(source, target);
            return target;
        } catch (Exception e) {
            log.error("Failed to create new instance of {}", targetClass.getName(), e);
            throw new RuntimeException("Failed to copy properties", e);
        }
    }
} 