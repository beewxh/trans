package com.hsbc.trans.controller.web;

import com.hsbc.common.errorhandler.bean.ErrorResponse;
import com.hsbc.common.errorhandler.exception.BusinessException;
import com.hsbc.common.response.CommonResponse;
import com.hsbc.trans.enums.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Web Controller Exception Handler
 * Handles all exceptions for web controllers
 */
@Slf4j
@Order(1)  // 确保这个处理器比 GlobalExceptionHandler 优先级高
@ControllerAdvice(annotations = WebController.class)
public class TransactionWebExceptionHandler {

    /**
     * Handle BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Object handleBusinessException(BusinessException e, HttpServletRequest request, HttpServletResponse response) {
        log.warn("Business exception occurred in web controller: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getCode());
        
        // 如果是 AJAX 请求，返回 JSON 响应
        if (isAjaxRequest(request)) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            return CommonResponse.fail(errorResponse);
        }
        
        // 否则返回错误页面
        return createErrorModelAndView(errorResponse);
    }

    /**
     * Handle parameter validation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Object handleValidationException(ConstraintViolationException e, HttpServletRequest request, HttpServletResponse response) {
        log.warn("Parameter validation failed in web controller: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.PARAM_ERROR.getCode());
        // 添加具体的验证错误信息
        errorResponse.setMessage(errorResponse.getMessage() + " (" + e.getMessage() + ")");
        
        // 如果是 AJAX 请求，返回 JSON 响应
        if (isAjaxRequest(request)) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            return CommonResponse.fail(errorResponse);
        }
        
        // 否则返回错误页面
        return createErrorModelAndView(errorResponse);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        log.error("Unexpected exception occurred in web controller", e);
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.SYSTEM_ERROR.getCode());
        
        // 如果是 AJAX 请求，返回 JSON 响应
        if (isAjaxRequest(request)) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            return CommonResponse.fail(errorResponse);
        }
        
        // 否则返回错误页面
        return createErrorModelAndView(errorResponse);
    }

    /**
     * Check if the request is an AJAX request
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        String accepts = request.getHeader("Accept");
        
        return "XMLHttpRequest".equals(requestedWith) || 
               (accepts != null && accepts.contains(MediaType.APPLICATION_JSON_VALUE));
    }

    /**
     * Create error page ModelAndView
     */
    private ModelAndView createErrorModelAndView(ErrorResponse errorResponse) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("code", errorResponse.getCode());
        mav.addObject("type", errorResponse.getType());
        mav.addObject("message", errorResponse.getMessage());
        return mav;
    }
} 