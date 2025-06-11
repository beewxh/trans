package com.hsbc.common.util;

import lombok.Data;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CopyBeanUtilsTest {

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

    // 测试用的类定义
    @Data
    static class SourceBean {
        private Long id;
        private String name;
        private BigDecimal amount;
        private Date createTime;
        private String extra;
    }

    @Data
    static class DestinationBean {
        private Long id;
        private String name;
        private BigDecimal amount;
        private Date createTime;
    }

    @Data
    static class DifferentFieldsBean {
        private Long id;
        private String name;
        private String code;
    }

    @Data
    static class BaseBean {
        private Long id;
        private String name;
    }

    @Data
    static class ChildSourceBean extends BaseBean {
        private String childField;
    }

    @Data
    static class ChildDestinationBean extends BaseBean {
        private String childField;
    }
} 