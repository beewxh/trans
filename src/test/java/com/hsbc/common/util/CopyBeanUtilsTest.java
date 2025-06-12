package com.hsbc.common.util;

import lombok.Data;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Test class for Bean property copy utility
 * Tests various functionalities of CopyBeanUtils class, including copying properties between objects
 * with same fields, different fields, and inheritance relationships
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
class CopyBeanUtilsTest {

    /**
     * Test copying properties between objects with same fields
     * Verifies the property copying functionality between objects that have identical fields
     */
    @Test
    void testCopyPropertiesWithSameFields() {
        // Prepare test data
        SourceBean source = new SourceBean();
        source.setId(1L);
        source.setName("Test");
        source.setAmount(new BigDecimal("100.00"));
        source.setCreateTime(new Date());

        // Test copying
        DestinationBean destination = new DestinationBean();
        CopyBeanUtils.copyProperties(source, destination);

        // Verify results
        assertEquals(source.getId(), destination.getId());
        assertEquals(source.getName(), destination.getName());
        assertEquals(source.getAmount(), destination.getAmount());
        assertEquals(source.getCreateTime(), destination.getCreateTime());
    }

    /**
     * Test copying properties between objects with different fields
     * Verifies the property copying functionality between objects with different field sets,
     * ensuring only common fields are copied
     */
    @Test
    void testCopyPropertiesWithDifferentFields() {
        // Prepare test data
        SourceBean source = new SourceBean();
        source.setId(1L);
        source.setName("Test");
        source.setAmount(new BigDecimal("100.00"));
        source.setCreateTime(new Date());
        source.setExtra("Extra Field"); // Field not in destination

        DifferentFieldsBean destination = new DifferentFieldsBean();
        destination.setCode("Original Code"); // Field not in source

        // Test copying
        CopyBeanUtils.copyProperties(source, destination);

        // Verify results
        assertEquals(source.getId(), destination.getId());
        assertEquals(source.getName(), destination.getName());
        assertEquals("Original Code", destination.getCode()); // Should not be overwritten
    }

    /**
     * Test copying properties between objects with inheritance relationship
     * Verifies the property copying functionality between objects with inheritance,
     * including fields from both parent and child classes
     */
    @Test
    void testCopyPropertiesWithInheritance() {
        // Prepare test data
        ChildSourceBean source = new ChildSourceBean();
        source.setId(1L);
        source.setName("Test");
        source.setChildField("Child Field");

        ChildDestinationBean destination = new ChildDestinationBean();
        
        // Test copying
        CopyBeanUtils.copyProperties(source, destination);

        // Verify results
        assertEquals(source.getId(), destination.getId());
        assertEquals(source.getName(), destination.getName());
        assertEquals(source.getChildField(), destination.getChildField());
    }

    /**
     * Test creating new instance and copying properties
     * Verifies the functionality of creating a new instance of target class and copying properties
     */
    @Test
    void testCopyPropertiesToNewInstance() {
        // Prepare test data
        SourceBean source = new SourceBean();
        source.setId(1L);
        source.setName("Test");
        source.setAmount(new BigDecimal("100.00"));

        // Test copying to new instance
        DestinationBean destination = CopyBeanUtils.copyProperties(source, DestinationBean.class);

        // Verify results
        assertNotNull(destination);
        assertEquals(source.getId(), destination.getId());
        assertEquals(source.getName(), destination.getName());
        assertEquals(source.getAmount(), destination.getAmount());
    }

    /**
     * Test copying properties with null values
     * Verifies the property copying functionality when source contains null values
     */
    @Test
    void testCopyPropertiesWithNullValues() {
        // Prepare test data
        SourceBean source = new SourceBean();
        source.setId(1L);
        // name is set to null
        source.setAmount(new BigDecimal("100.00"));

        DestinationBean destination = new DestinationBean();
        destination.setName("Original Name");

        // Test copying
        CopyBeanUtils.copyProperties(source, destination);

        // Verify results
        assertEquals(source.getId(), destination.getId());
        assertNull(destination.getName()); // null value should be copied
        assertEquals(source.getAmount(), destination.getAmount());
    }

    /**
     * Source bean class
     * Source data object for testing
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
     * Destination bean class
     * Target data object for testing, has same fields as source object
     */
    @Data
    static class DestinationBean {
        private Long id;
        private String name;
        private BigDecimal amount;
        private Date createTime;
    }

    /**
     * Different fields bean class
     * Target data object for testing, has different fields from source object
     */
    @Data
    static class DifferentFieldsBean {
        private Long id;
        private String name;
        private String code;
    }

    /**
     * Base bean class
     * Base class for testing inheritance
     */
    @Data
    static class BaseBean {
        private Long id;
        private String name;
    }

    /**
     * Child source bean class
     * Source child class for testing inheritance
     */
    @Data
    static class ChildSourceBean extends BaseBean {
        private String childField;
    }

    /**
     * Child destination bean class
     * Target child class for testing inheritance
     */
    @Data
    static class ChildDestinationBean extends BaseBean {
        private String childField;
    }
} 