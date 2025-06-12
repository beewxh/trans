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
 * Global Exception Handler
 * Used to handle various exceptions thrown in the system and provide standardized exception responses
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle business exceptions
     * Records logs at different levels based on the exception's error level and returns a standardized error response
     *
     * @param ex Business exception
     * @return Response entity containing error information
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Object>> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(ex.getCode());
        if (ErrorLevel.ERROR.equals(ErrorLevel.getByCode(error.getLevel()))) {
            log.error("Business exception caught by global handler, error code: {}, error message: {}", ex.getCode(), ex.getMessage(), ex);
        } else if (ErrorLevel.WARN.equals(ErrorLevel.getByCode(error.getLevel()))) {
            log.warn("Business warning caught by global handler, error code: {}, error message: {}", ex.getCode(), ex.getMessage(), ex);
        } else {
            log.info("Business notification caught by global handler, error code: {}, error message: {}", ex.getCode(), ex.getMessage(), ex);
        }
        if (ex instanceof ParamValidationException) {
            error.setMessage(error.getMessage() + "(" + ex.getMessage() + ")");
        }
        return new ResponseEntity<>(CommonResponse.fail(error), HttpStatus.OK);
    }

    /**
     * Handle parameter validation exceptions
     * Handles parameter validation exceptions thrown by the Jakarta Validation framework
     *
     * @param ex Parameter validation exception
     * @return Response entity containing error information
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<Object>> handleGlobalException(ConstraintViolationException ex) {
        ErrorResponse error = new ErrorResponse(ErrorCode.PARAM_ERROR.getCode());
        error.setMessage(error.getMessage() + "(" + ex.getMessage() + ")");
        log.error("Parameter validation exception caught by global handler, error code: {}, error message: {}", error.getCode(), ex.getMessage(), ex);
        return new ResponseEntity<>(CommonResponse.fail(error), HttpStatus.OK);
    }

    /**
     * Handle system exceptions
     * Handles all exceptions not caught by other exception handlers
     *
     * @param ex System exception
     * @return Response entity containing error information
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Object>> handleGlobalException(Exception ex) {
        ErrorResponse error = new ErrorResponse(ErrorCode.SYSTEM_ERROR.getCode());
        log.error("System exception caught by global handler, error code: {}, error message: {}", error.getCode(), ex.getMessage(), ex);
        return new ResponseEntity<>(CommonResponse.fail(error), HttpStatus.OK);
    }
} 