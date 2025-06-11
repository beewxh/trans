package com.hsbc.common.errorhandler.exception;

import lombok.Data;

/**
 * 业务异常类
 */
@Data
public class BusinessException extends RuntimeException {

    private String code;

    public BusinessException() {
        super();
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }


    public BusinessException(Exception e, String code) {
        super(e);
        this.code = code;
    }

    public BusinessException(String message, String code) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable e, String code) {
        super(message, e);
        this.code = code;
    }

    public BusinessException code(String code) {
        this.code = code;
        return this;
    }

}
