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
 * Bean attribute copy utility class
 * Provides functionality for copying object attributes based on reflection, supporting deep attribute copying and type conversion
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Slf4j
public class CopyBeanUtils {
    /**
     * Cache class field information for performance
     */
    private static final Map<Class<?>, List<Field>> FIELDS_CACHE = new ConcurrentHashMap<>();

    /**
     * Copy the attributes of the source object to the target object
     * Only copy fields with matching names and types
     *
     * @param source Source object
     * @param destination Target object
     * @param <S> Source object type
     * @param <D> Target object type
     */
    public static <S, D> void copyProperties(S source, D destination) {
        if (source == null || destination == null) {
            return;
        }

        List<Field> sourceFields = getFields(source.getClass());
        List<Field> destFields = getFields(destination.getClass());

        // Iterate over the fields of the target object
        for (Field destField : destFields) {
            // Find the corresponding field in the source object with the same name and type
            sourceFields.stream()
                .filter(sourceField -> isFieldMatch(sourceField, destField))
                .findFirst()
                .ifPresent(sourceField -> copyFieldValue(source, destination, sourceField, destField));
        }
    }

    /**
     * Get all fields (including parent class fields) of a class, and cache the result
     *
     * @param clazz Class to get fields from
     * @return List of all fields in the class
     */
    private static List<Field> getFields(Class<?> clazz) {
        return FIELDS_CACHE.computeIfAbsent(clazz, k -> {
            List<Field> fields = Arrays.stream(k.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .collect(Collectors.toList());

            // Get the fields of the parent class
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
     * Determine if two fields match (both name and type are the same)
     *
     * @param sourceField Source field
     * @param destField Target field
     * @return If the fields match, return true; otherwise, return false
     */
    private static boolean isFieldMatch(Field sourceField, Field destField) {
        return sourceField.getName().equals(destField.getName()) &&
            sourceField.getType().equals(destField.getType());
    }

    /**
     * Copy field value
     *
     * @param source Source object
     * @param destination Target object
     * @param sourceField Source field
     * @param destField Target field
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
     * Create a new instance of the target class and copy attributes
     *
     * @param source Source object
     * @param targetClass Target class
     * @return The new instance after copying attributes
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