package com.hsbc.common.response;

import com.hsbc.common.errorhandler.bean.ErrorResponse;
import com.hsbc.common.response.enums.ResponseCode;
import lombok.Data;

/**
 * 通用响应对象
 * 用于统一系统的响应格式，支持成功和失败两种状态
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 * @param <T> 响应数据的类型
 */
@Data
public class CommonResponse<T> {

    /**
     * 响应码
     */
    String code;

    /**
     * 响应数据
     */
    T data;

    /**
     * 错误信息
     */
    ErrorResponse error;

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @return 成功的响应对象
     * @param <T> 响应数据的类型
     */
    public static <T> CommonResponse<T> succeed(T data) {
        CommonResponse<T> response = new CommonResponse<>();
        response.code = ResponseCode.SUCC.getCode();
        response.data = data;
        return response;
    }

    /**
     * 创建失败响应
     *
     * @param error 错误信息
     * @return 失败的响应对象
     * @param <T> 响应数据的类型
     */
    public static <T> CommonResponse<T> fail(ErrorResponse error) {
        CommonResponse<T> response = new CommonResponse<>();
        response.code = error.getCode();
        response.error = error;
        return response;
    }
}
