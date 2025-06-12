package com.hsbc.trans.enums;

import com.hsbc.common.errorhandler.enums.ErrorLevel;
import com.hsbc.common.errorhandler.enums.ErrorType;
import com.hsbc.common.util.JsonUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Error Code Enumeration
 * Defines all error codes in the system, including system errors and business errors
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Getter
@Slf4j
public enum ErrorCode {

    /**
     * System error, indicates an unknown internal system exception
     */
    SYSTEM_ERROR("000001", "System error, please try again later", ErrorType.SYSTEM_ERROR, ErrorLevel.ERROR),

    /**
     * Parameter error, indicates interface parameter validation failure
     */
    PARAM_ERROR("000002", "Parameter error", ErrorType.PARAM_ERROR, ErrorLevel.INFO),

    /**
     * Unknown error, indicates undefined error type
     */
    UNKNOWN_ERROR("000003", "Unknown error", ErrorType.SYSTEM_ERROR, ErrorLevel.ERROR),

    /**
     * Transaction order not found error
     */
    TRANSACTION_NOT_FOUND("100001", "Transaction not found"),

    /**
     * Duplicate transaction order error
     */
    TRANSACTION_DUPLICATE("100002", "Transaction already exists", ErrorType.BUSINESS_ERROR, ErrorLevel.WARN),

    /**
     * Transaction order unchanged error
     */
    TRANSACTION_NOT_CHANGED("100003", "No changes made to transaction during update", ErrorType.BUSINESS_ERROR, ErrorLevel.WARN),

    /**
     * Invalid transaction status change error
     */
    TRANSACTION_UPDATE_STATUS_INVALID("100004", "Invalid transaction status transition");

    /**
     * Error code
     */
    private final String code;

    /**
     * Error message
     */
    private final String msg;

    /**
     * Error type
     */
    private final ErrorType type;

    /**
     * Error level
     */
    private final ErrorLevel level;

    /**
     * Constructor
     *
     * @param code Error code
     * @param msg Error message
     * @param type Error type
     * @param level Error level
     */
    ErrorCode(String code, String msg, ErrorType type, ErrorLevel level) {
        this.msg = msg;
        this.code = code;
        this.type = type;
        this.level = level;
    }

    /**
     * Constructor, uses default business error type and error level
     *
     * @param code Error code
     * @param msg Error message
     */
    ErrorCode(String code, String msg) {
        this.msg = msg;
        this.code = code;
        this.type = ErrorType.BUSINESS_ERROR;
        this.level = ErrorLevel.ERROR;
    }

    /**
     * Get error code enumeration instance by error code
     *
     * @param code Error code
     * @return Error code enumeration instance, returns UNKNOWN_ERROR if not found
     */
    public static ErrorCode getByCode(String code) {
        ErrorCode[] errors = ErrorCode.values();
        for (ErrorCode ec : errors) {
            if (ec.getCode().equals(code)) {
                return ec;
            }
        }
        log.warn("code not found while invoking getByCode: {}", code);
        return UNKNOWN_ERROR;
    }

    /**
     * Convert to JSON string
     *
     * @return Error code information in JSON format
     */
    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
