package com.hsbc.common.errorhandler.exception;

/**
 * 框架异常类
 */
public class FrameworkException extends RuntimeException{

    public FrameworkException() {
        super();
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(Throwable cause) {
        super(cause);
    }

}
