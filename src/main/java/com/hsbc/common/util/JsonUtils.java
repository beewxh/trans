package com.hsbc.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hsbc.common.errorhandler.exception.FrameworkException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON Utility Class
 * Provides JSON serialization and deserialization functionality based on Jackson
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public class JsonUtils {
    /**
     * ObjectMapper instance for JSON operations
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Convert an object to a JSON string
     *
     * @param obj The object to convert
     * @return JSON string, returns null if the object is null
     * @throws FrameworkException if an error occurs during conversion
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new FrameworkException("Failed to convert to JSON string", e);
        }
    }

    /**
     * Convert a JSON string to an object of the specified type
     *
     * @param json JSON string
     * @param clazz Target class type
     * @return Converted object, returns null if the JSON string is empty
     * @throws FrameworkException if an error occurs during conversion
     * @param <T> Target type
     */
    public static <T> T toBean(String json, Class<T> clazz) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new FrameworkException("Failed to parse JSON string", e);
        }
    }

    /**
     * Convert a JSON string to an object of the specified type (supports generics)
     *
     * @param json JSON string
     * @param typeReference Type reference
     * @return Converted object, returns null if the JSON string is empty
     * @throws FrameworkException if an error occurs during conversion
     * @param <T> Target type
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new FrameworkException("Failed to parse JSON string", e);
        }
    }

    /**
     * Convert a JSON string to an object of the specified type
     *
     * @param json JSON string
     * @param clazz Target class type
     * @return Converted object
     * @param <T> Target type
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return toBean(json, clazz);
    }

    /**
     * Convert a JSON string to a Map object
     *
     * @param json JSON string
     * @return Map object
     */
    public static Map<String, Object> toMap(String json) {
        return toBean(json, Map.class);
    }

    /**
     * Convert a JSON string to a Map object of specified types
     *
     * @param json JSON string
     * @param keyClass Key type of the Map
     * @param valueClass Value type of the Map
     * @return Map object, returns null if the JSON string is empty
     * @throws FrameworkException if an error occurs during conversion
     * @param <K> Key type of the Map
     * @param <V> Value type of the Map
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> keyClass, Class<V> valueClass) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            JavaType type = MAPPER.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new FrameworkException("Failed to parse JSON string to Map", e);
        }
    }

    /**
     * Convert a JSON string to a List object of specified type
     *
     * @param json JSON string
     * @param elementClass Element type of the List
     * @return List object, returns empty List if the JSON string is empty
     * @throws FrameworkException if an error occurs during conversion
     * @param <T> Element type of the List
     */
    public static <T> List<T> toList(String json, Class<T> elementClass) {
        if (!StringUtils.hasText(json)) {
            return new ArrayList<>();
        }
        try {
            JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass);
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new FrameworkException("Failed to parse JSON string to List", e);
        }
    }

    /**
     * Convert a JSON string to a JsonNode object
     *
     * @param json JSON string
     * @return JsonNode object
     * @throws FrameworkException if an error occurs during conversion
     */
    public static JsonNode parseNode(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new FrameworkException("Failed to parse JSON string to JsonNode", e);
        }
    }

    /**
     * Create a new ObjectNode
     *
     * @return ObjectNode object
     */
    public static ObjectNode createObjectNode() {
        return MAPPER.createObjectNode();
    }

    /**
     * Get the underlying ObjectMapper instance
     *
     * @return ObjectMapper instance
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }
} 