package com.hsbc.common.errorhandler.exception;

/**
 * 框架异常类
 * 用于表示框架层面的异常情况，如配置错误、反射调用失败等
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public class FrameworkException extends RuntimeException {

    /**
     * 默认构造函数
     */
    public FrameworkException() {
        super();
    }

    /**
     * 使用异常消息和原因构造异常
     *
     * @param message 异常消息
     * @param cause 原因
     */
    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 使用异常消息构造异常
     *
     * @param message 异常消息
     */
    public FrameworkException(String message) {
        super(message);
    }

    /**
     * 使用原因构造异常
     *
     * @param cause 原因
     */
    public FrameworkException(Throwable cause) {
        super(cause);
    }
}
