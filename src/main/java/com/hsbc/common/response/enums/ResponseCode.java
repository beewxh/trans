package com.hsbc.common.response.enums;

import lombok.Getter;

/**
 * Response Code Enumeration
 * Defines system response status codes and corresponding messages
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public enum ResponseCode {

    /**
     * Success response code
     */
    SUCC("000000", "Request successful");

    /**
     * Response code
     */
    @Getter
    private final String code;

    /**
     * Response message
     */
    private final String msg;

    /**
     * Constructor
     *
     * @param code Response code
     * @param msg Response message
     */
    ResponseCode(String code, String msg) {
        this.msg = msg;
        this.code = code;
    }
}
