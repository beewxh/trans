package com.hsbc.common.errorhandler.enums;

/**
 * 错误类型枚举
 * 定义系统中可能出现的各种错误类型
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public enum ErrorType {
    /**
     * 业务错误，表示业务逻辑处理过程中的错误
     */
    BUSINESS_ERROR,
    /**
     * 参数异常，表示接口参数验证失败
     */
    PARAM_ERROR,
    /**
     * 系统错误，表示系统内部错误或未知错误
     */
    SYSTEM_ERROR,
    /**
     * 签名错误，表示请求签名验证失败
     */
    SIGN_ERROR,
    /**
     * 通信错误，表示网络通信或外部服务调用失败
     */
    COMMUNICATION_ERROR;
}