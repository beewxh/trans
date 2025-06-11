package com.hsbc.common.response.enums;

import lombok.Getter;

/**
 * 响应码枚举
 * 定义系统响应的状态码和对应的消息
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public enum ResponseCode {

    /**
     * 成功响应码
     */
    SUCC("000000", "请求成功");

    /**
     * 响应码
     */
    @Getter
    private final String code;

    /**
     * 响应消息
     */
    private final String msg;

    /**
     * 构造函数
     *
     * @param code 响应码
     * @param msg 响应消息
     */
    ResponseCode(String code, String msg) {
        this.msg = msg;
        this.code = code;
    }
}
