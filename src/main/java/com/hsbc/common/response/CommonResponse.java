package com.hsbc.common.response;

import com.hsbc.common.errorhandler.bean.ErrorResponse;
import com.hsbc.common.response.enums.ResponseCode;
import lombok.Data;

/**
 * Common Response Object
 * Used to standardize system response format, supporting both success and failure states
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 * @param <T> Response data type
 */
@Data
public class CommonResponse<T> {

    /**
     * Response code
     */
    String code;

    /**
     * Response data
     */
    T data;

    /**
     * Error message
     */
    ErrorResponse error;

    /**
     * Create success response
     *
     * @param data Response data
     * @return Success response object
     * @param <T> Response data type
     */
    public static <T> CommonResponse<T> succeed(T data) {
        CommonResponse<T> response = new CommonResponse<>();
        response.code = ResponseCode.SUCC.getCode();
        response.data = data;
        return response;
    }

    /**
     * Create failure response
     *
     * @param error Error message
     * @return Failure response object
     * @param <T> Response data type
     */
    public static <T> CommonResponse<T> fail(ErrorResponse error) {
        CommonResponse<T> response = new CommonResponse<>();
        response.code = error.getCode();
        response.error = error;
        return response;
    }
}
