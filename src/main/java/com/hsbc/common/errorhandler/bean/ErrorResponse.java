package com.hsbc.common.errorhandler.bean;

import com.hsbc.trans.enums.ErrorCode;
import lombok.Data;

/**
 * Error Response Object
 * Used to encapsulate system error information, including error code, message, type, and level
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Data
public class ErrorResponse {
    /**
     * Error code
     */
    private String code;

    /**
     * Error message
     */
    private String message;

    /**
     * Error type
     */
    private String type;

    /**
     * Error level
     */
    private int level;

    /**
     * Constructor
     * Creates error response object based on error code, automatically fills error message, type, and level
     *
     * @param code Error code
     */
    public ErrorResponse(String code) {
        ErrorCode ec = ErrorCode.getByCode(code);
        this.message = ec.getMsg();
        this.type = ec.getType().name();
        this.level = ec.getLevel().getCode();
        if (ec.equals(ErrorCode.UNKNOWN_ERROR)) {
            this.code = code;
        } else {
            this.code = code;
        }
    }
} 