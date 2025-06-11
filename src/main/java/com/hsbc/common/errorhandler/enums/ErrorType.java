package com.hsbc.common.errorhandler.enums;

public enum ErrorType {
    /**
     * 业务错误
     */
    BUSINESS_ERROR,
    /**
     * 参数异常
     */
    PARAM_ERROR,
    /**
     * 系统错误
     */
    SYSTEM_ERROR,
    /**
     * 签名错误
     */
    SIGN_ERROR,
    /**
     * 通信错误
     */
    COMMUNICATION_ERROR;
}