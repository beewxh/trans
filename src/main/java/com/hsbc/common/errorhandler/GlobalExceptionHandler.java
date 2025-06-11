package com.hsbc.common.errorhandler;

import com.hsbc.common.errorhandler.exception.ParamValidationException;
import com.hsbc.trans.enums.ErrorCode;
import com.hsbc.common.errorhandler.exception.BusinessException;
import com.hsbc.common.response.CommonResponse;
import com.hsbc.common.errorhandler.enums.ErrorLevel;
import com.hsbc.common.errorhandler.bean.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理器
 * 用于统一处理系统中抛出的各类异常，提供标准的异常响应格式
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 根据异常的错误级别记录不同级别的日志，并返回标准的错误响应
     *
     * @param ex 业务异常
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Object>> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(ex.getCode());
        if (ErrorLevel.ERROR.equals(ErrorLevel.getByCode(error.getLevel()))) {
            log.error("业务异常被全局异常处理器捕获，错误码: {}, 错误信息: {}", ex.getCode(), ex.getMessage(), ex);
        } else if (ErrorLevel.WARN.equals(ErrorLevel.getByCode(error.getLevel()))) {
            log.warn("业务警告被全局异常处理器捕获，错误码: {}, 错误信息: {}", ex.getCode(), ex.getMessage(), ex);
        } else {
            log.info("业务提示被全局异常处理器捕获，错误码: {}, 错误信息: {}", ex.getCode(), ex.getMessage(), ex);
        }
        if (ex instanceof ParamValidationException) {
            error.setMessage(error.getMessage() + "(" + ex.getMessage() + ")");
        }
        return new ResponseEntity<>(CommonResponse.fail(error), HttpStatus.OK);
    }

    /**
     * 处理参数校验异常
     * 处理由Jakarta Validation框架抛出的参数校验异常
     *
     * @param ex 参数校验异常
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<Object>> handleGlobalException(ConstraintViolationException ex) {
        ErrorResponse error = new ErrorResponse(ErrorCode.PARAM_ERROR.getCode());
        error.setMessage(error.getMessage() + "(" + ex.getMessage() + ")");
        log.error("参数校验异常被全局异常处理器捕获，错误码: {}, 错误信息: {}", error.getCode(), ex.getMessage(), ex);
        return new ResponseEntity<>(CommonResponse.fail(error), HttpStatus.OK);
    }

    /**
     * 处理系统异常
     * 处理所有未被其他异常处理器捕获的异常
     *
     * @param ex 系统异常
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Object>> handleGlobalException(Exception ex) {
        ErrorResponse error = new ErrorResponse(ErrorCode.SYSTEM_ERROR.getCode());
        log.error("系统异常被全局异常处理器捕获，错误码: {}, 错误信息: {}", error.getCode(), ex.getMessage(), ex);
        return new ResponseEntity<>(CommonResponse.fail(error), HttpStatus.OK);
    }
} 