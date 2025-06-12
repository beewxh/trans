package com.hsbc.common.errorhandler.enums;

/**
 * Error Type Enumeration
 * Defines different types of errors in the system
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public enum ErrorType {
    /**
     * System error, indicates internal system exceptions
     */
    SYSTEM_ERROR,
    /**
     * Parameter error, indicates parameter validation failures
     */
    PARAM_ERROR,
    /**
     * Business error, indicates business rule violations
     */
    BUSINESS_ERROR,
    /**
     * Error type code
     */
    SIGN_ERROR,
    /**
     * Communication error, indicates network communication or external service call failures
     */
    COMMUNICATION_ERROR;
}