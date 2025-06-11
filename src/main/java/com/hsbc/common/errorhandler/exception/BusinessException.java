package com.hsbc.common.errorhandler.exception;

import lombok.Data;

/**
 * 业务异常类
 * 用于表示业务处理过程中的异常情况，支持错误码和错误消息
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Data
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private String code;

    /**
     * 默认构造函数
     */
    public BusinessException() {
        super();
    }

    /**
     * 使用异常消息和原因构造异常
     *
     * @param message 异常消息
     * @param cause 原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 使用异常消息构造异常
     *
     * @param message 异常消息
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * 使用原因构造异常
     *
     * @param cause 原因
     */
    public BusinessException(Throwable cause) {
        super(cause);
    }

    /**
     * 使用异常和错误码构造异常
     *
     * @param e 异常
     * @param code 错误码
     */
    public BusinessException(Exception e, String code) {
        super(e);
        this.code = code;
    }

    /**
     * 使用异常消息和错误码构造异常
     *
     * @param message 异常消息
     * @param code 错误码
     */
    public BusinessException(String message, String code) {
        super(message);
        this.code = code;
    }

    /**
     * 使用异常消息、原因和错误码构造异常
     *
     * @param message 异常消息
     * @param e 原因
     * @param code 错误码
     */
    public BusinessException(String message, Throwable e, String code) {
        super(message, e);
        this.code = code;
    }

    /**
     * 设置错误码并返回异常实例
     *
     * @param code 错误码
     * @return 当前异常实例
     */
    public BusinessException code(String code) {
        this.code = code;
        return this;
    }
}
