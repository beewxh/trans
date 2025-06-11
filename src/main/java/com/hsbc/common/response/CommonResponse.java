package com.hsbc.common.response;

import com.hsbc.common.errorhandler.bean.ErrorResponse;
import com.hsbc.common.response.enums.ResponseCode;
import lombok.Data;

/**
 * @ClassName CommonResponse
 * @Description TODO
 * @Author rd
 * @Date 2025/6/11 19:04
 * @Version 1.0
 **/
@Data
public class CommonResponse<T> {

    String code;
    T data;
    ErrorResponse error;


    public static <T> CommonResponse<T> succeed(T data) {
        CommonResponse<T> response = new CommonResponse<>();
        response.code = ResponseCode.SUCC.getCode();
        response.data = data;
        return response;
    }

    public static <T> CommonResponse<T> fail(ErrorResponse error) {
        CommonResponse<T> response = new CommonResponse<>();
        response.code = error.getCode();
        response.error = error;
        return response;
    }

}
