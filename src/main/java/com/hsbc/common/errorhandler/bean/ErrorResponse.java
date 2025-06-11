package com.hsbc.common.errorhandler.bean;

import com.hsbc.trans.enums.ErrorCode;
import lombok.Data;

/**
 * 错误响应对象
 * 用于封装系统错误信息，包含错误码、错误消息、错误类型和错误级别
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Data
public class ErrorResponse {
    /**
     * 错误码
     */
    private String code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 错误类型
     */
    private String type;

    /**
     * 错误级别
     */
    private int level;

    /**
     * 构造函数
     * 根据错误码创建错误响应对象，自动填充错误消息、类型和级别
     *
     * @param code 错误码
     */
    public ErrorResponse(String code) {
        ErrorCode ec = ErrorCode.getByCode(code);
        this.message = ec.getMsg();
        this.type = ec.getType().name();
        this.level = ec.getLevel().getCode();
        if (ec.equals(ErrorCode.UNKNOWN_ERROR)) {
            this.code = code;
        } else {
            this.code = code;
        }
    }
} 