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
 * JSON工具类
 * 提供基于Jackson的JSON序列化和反序列化功能
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public class JsonUtils {
    /**
     * Jackson的ObjectMapper实例，用于JSON操作
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转换为JSON字符串
     *
     * @param obj 要转换的对象
     * @return JSON字符串，如果对象为null则返回null
     * @throws FrameworkException 如果转换过程中发生错误
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new FrameworkException("转换JSON字符串失败", e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的对象
     *
     * @param json JSON字符串
     * @param clazz 目标类型
     * @return 转换后的对象，如果JSON字符串为空则返回null
     * @throws FrameworkException 如果转换过程中发生错误
     * @param <T> 目标类型
     */
    public static <T> T toBean(String json, Class<T> clazz) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new FrameworkException("解析JSON字符串失败", e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的对象（支持泛型）
     *
     * @param json JSON字符串
     * @param typeReference 类型引用
     * @return 转换后的对象，如果JSON字符串为空则返回null
     * @throws FrameworkException 如果转换过程中发生错误
     * @param <T> 目标类型
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new FrameworkException("解析JSON字符串失败", e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的对象
     *
     * @param json JSON字符串
     * @param clazz 目标类型
     * @return 转换后的对象
     * @param <T> 目标类型
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return toBean(json, clazz);
    }

    /**
     * 将JSON字符串转换为Map对象
     *
     * @param json JSON字符串
     * @return Map对象
     */
    public static Map<String, Object> toMap(String json) {
        return toBean(json, Map.class);
    }

    /**
     * 将JSON字符串转换为指定类型的Map对象
     *
     * @param json JSON字符串
     * @param keyClass Map的key类型
     * @param valueClass Map的value类型
     * @return Map对象，如果JSON字符串为空则返回null
     * @throws FrameworkException 如果转换过程中发生错误
     * @param <K> Map的key类型
     * @param <V> Map的value类型
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> keyClass, Class<V> valueClass) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            JavaType type = MAPPER.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new FrameworkException("解析JSON字符串到Map失败", e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的List对象
     *
     * @param json JSON字符串
     * @param elementClass List元素类型
     * @return List对象，如果JSON字符串为空则返回空List
     * @throws FrameworkException 如果转换过程中发生错误
     * @param <T> List元素类型
     */
    public static <T> List<T> toList(String json, Class<T> elementClass) {
        if (!StringUtils.hasText(json)) {
            return new ArrayList<>();
        }
        try {
            JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass);
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new FrameworkException("解析JSON字符串到List失败", e);
        }
    }

    /**
     * 将JSON字符串转换为JsonNode对象
     *
     * @param json JSON字符串
     * @return JsonNode对象
     * @throws FrameworkException 如果转换过程中发生错误
     */
    public static JsonNode parseNode(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new FrameworkException("解析JSON字符串到JsonNode失败", e);
        }
    }

    /**
     * 创建一个新的ObjectNode
     *
     * @return ObjectNode对象
     */
    public static ObjectNode createObjectNode() {
        return MAPPER.createObjectNode();
    }

    /**
     * 获取底层的ObjectMapper实例
     *
     * @return ObjectMapper实例
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }
} 