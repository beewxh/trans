package com.hsbc.common.response.enums;

public enum ResponseCode {

    SUCC("000000", "请求成功");

    private final String code;
    private final String msg;

    ResponseCode(String code, String msg) {
        this.msg = msg;
        this.code = code;
    }
    public String getCode() {
        return code;
    }

}
