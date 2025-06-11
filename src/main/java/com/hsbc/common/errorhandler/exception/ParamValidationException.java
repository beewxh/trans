package com.hsbc.common.errorhandler.exception;

/**
 * 参数验证异常类
 * 用于表示参数验证失败的异常情况，继承自业务异常类
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public class ParamValidationException extends BusinessException {

    /**
     * 使用异常消息构造异常
     *
     * @param message 异常消息
     */
    public ParamValidationException(String message) {
        super(message);
    }
}
