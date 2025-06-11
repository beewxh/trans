package com.hsbc.common.errorhandler;

import com.hsbc.trans.enums.ErrorCode;
import com.hsbc.common.errorhandler.exception.BusinessException;
import com.hsbc.common.response.CommonResponse;
import com.hsbc.common.errorhandler.enums.ErrorLevel;
import com.hsbc.common.errorhandler.bean.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Object>> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(ex.getCode());
        if (ErrorLevel.ERROR.equals(ErrorLevel.getByCode(error.getLevel()))) {
            log.error("business error caught by exception handler, code: {}, msg: {}", ex.getCode(), ex.getMessage(), ex);
        } else if (ErrorLevel.WARN.equals(ErrorLevel.getByCode(error.getLevel()))) {
            log.warn("business error caught by exception handler, code: {}, msg: {}", ex.getCode(), ex.getMessage(), ex);
        } else {
            log.info("business error caught by exception handler, code: {}, msg: {}", ex.getCode(), ex.getMessage(), ex);
        }
        return new ResponseEntity<>(CommonResponse.fail(error), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Object>> handleGlobalException(Exception ex) {
        ErrorResponse error = new ErrorResponse(ErrorCode.SYSTEM_ERROR.getCode());
        log.error("system error caught by exception handler, code: {}, msg: {}", error.getCode(), ex.getMessage(), ex);
        return new ResponseEntity<>(CommonResponse.fail(error), HttpStatus.OK);
    }
} 