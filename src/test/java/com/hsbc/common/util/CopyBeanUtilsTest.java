package com.hsbc.common.util;

import lombok.Data;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Bean属性复制工具类测试
 * 用于测试CopyBeanUtils类的各项功能，包括相同字段复制、不同字段复制、继承关系复制等
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
class CopyBeanUtilsTest {

    /**
     * 测试相同字段的属性复制
     * 验证具有相同字段的对象之间的属性复制功能
     */
    @Test
    void testCopyPropertiesWithSameFields() {
        // 准备测试数据
        SourceBean source = new SourceBean();
        source.setId(1L);
        source.setName("测试");
        source.setAmount(new BigDecimal("100.00"));
        source.setCreateTime(new Date());

        // 测试拷贝
        DestinationBean destination = new DestinationBean();
        CopyBeanUtils.copyProperties(source, destination);

        // 验证结果
        assertEquals(source.getId(), destination.getId());
        assertEquals(source.getName(), destination.getName());
        assertEquals(source.getAmount(), destination.getAmount());
        assertEquals(source.getCreateTime(), destination.getCreateTime());
    }

    /**
     * 测试不同字段的属性复制
     * 验证具有不同字段的对象之间的属性复制功能，确保只复制共同字段
     */
    @Test
    void testCopyPropertiesWithDifferentFields() {
        // 准备测试数据
        SourceBean source = new SourceBean();
        source.setId(1L);
        source.setName("测试");
        source.setAmount(new BigDecimal("100.00"));
        source.setCreateTime(new Date());
        source.setExtra("额外字段"); // 目标对象没有这个字段

        DifferentFieldsBean destination = new DifferentFieldsBean();
        destination.setCode("原始代码"); // 源对象没有这个字段

        // 测试拷贝
        CopyBeanUtils.copyProperties(source, destination);

        // 验证结果
        assertEquals(source.getId(), destination.getId());
        assertEquals(source.getName(), destination.getName());
        assertEquals("原始代码", destination.getCode()); // 不应该被覆盖
    }

    /**
     * 测试继承关系的属性复制
     * 验证具有继承关系的对象之间的属性复制功能，包括父类和子类的字段
     */
    @Test
    void testCopyPropertiesWithInheritance() {
        // 准备测试数据
        ChildSourceBean source = new ChildSourceBean();
        source.setId(1L);
        source.setName("测试");
        source.setChildField("子类字段");

        ChildDestinationBean destination = new ChildDestinationBean();
        
        // 测试拷贝
        CopyBeanUtils.copyProperties(source, destination);

        // 验证结果
        assertEquals(source.getId(), destination.getId());
        assertEquals(source.getName(), destination.getName());
        assertEquals(source.getChildField(), destination.getChildField());
    }

    /**
     * 测试创建新实例并复制属性
     * 验证创建目标类新实例并复制属性的功能
     */
    @Test
    void testCopyPropertiesToNewInstance() {
        // 准备测试数据
        SourceBean source = new SourceBean();
        source.setId(1L);
        source.setName("测试");
        source.setAmount(new BigDecimal("100.00"));

        // 测试拷贝到新实例
        DestinationBean destination = CopyBeanUtils.copyProperties(source, DestinationBean.class);

        // 验证结果
        assertNotNull(destination);
        assertEquals(source.getId(), destination.getId());
        assertEquals(source.getName(), destination.getName());
        assertEquals(source.getAmount(), destination.getAmount());
    }

    /**
     * 测试空值的属性复制
     * 验证包含null值的属性复制功能
     */
    @Test
    void testCopyPropertiesWithNullValues() {
        // 准备测试数据
        SourceBean source = new SourceBean();
        source.setId(1L);
        // name设置为null
        source.setAmount(new BigDecimal("100.00"));

        DestinationBean destination = new DestinationBean();
        destination.setName("原始名称");

        // 测试拷贝
        CopyBeanUtils.copyProperties(source, destination);

        // 验证结果
        assertEquals(source.getId(), destination.getId());
        assertNull(destination.getName()); // null值应该被拷贝
        assertEquals(source.getAmount(), destination.getAmount());
    }

    /**
     * 源对象类
     * 用于测试的源数据对象
     */
    @Data
    static class SourceBean {
        private Long id;
        private String name;
        private BigDecimal amount;
        private Date createTime;
        private String extra;
    }

    /**
     * 目标对象类
     * 用于测试的目标数据对象，与源对象具有相同的字段
     */
    @Data
    static class DestinationBean {
        private Long id;
        private String name;
        private BigDecimal amount;
        private Date createTime;
    }

    /**
     * 不同字段对象类
     * 用于测试的目标数据对象，与源对象具有不同的字段
     */
    @Data
    static class DifferentFieldsBean {
        private Long id;
        private String name;
        private String code;
    }

    /**
     * 基础对象类
     * 用于测试继承关系的基类
     */
    @Data
    static class BaseBean {
        private Long id;
        private String name;
    }

    /**
     * 子类源对象
     * 用于测试继承关系的源数据子类
     */
    @Data
    static class ChildSourceBean extends BaseBean {
        private String childField;
    }

    /**
     * 子类目标对象
     * 用于测试继承关系的目标数据子类
     */
    @Data
    static class ChildDestinationBean extends BaseBean {
        private String childField;
    }
} 