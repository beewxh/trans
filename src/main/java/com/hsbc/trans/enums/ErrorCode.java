package com.hsbc.trans.enums;


import com.hsbc.common.errorhandler.enums.ErrorLevel;
import com.hsbc.common.errorhandler.enums.ErrorType;
import com.hsbc.trans.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ErrorCode {


    /**
     * 通用错误码
     */
    SYSTEM_ERROR("000001", "系统异常，请稍后再试", ErrorType.SYSTEM_ERROR, ErrorLevel.ERROR),
    PARAM_ERROR("000002", "参数错误", ErrorType.PARAM_ERROR, ErrorLevel.INFO),
    UNKNOWN_ERROR("000003", "未知错误", ErrorType.SYSTEM_ERROR, ErrorLevel.ERROR),

    /**
     * 业务错误码
     */
    TRANSACTION_NOT_FOUND("100001", "交易订单未找到", ErrorType.BUSINESS_ERROR, ErrorLevel.ERROR),


    ;


    private final String code;
    private final String msg;
    private final ErrorType type;
    private final ErrorLevel level;


    ErrorCode(String code, String msg, ErrorType type, ErrorLevel level) {
        this.msg = msg;
        this.code = code;
        this.type = type;
        this.level = level;
    }

    ErrorCode(String code, String msg) {
        this.msg = msg;
        this.code = code;
        this.type = ErrorType.BUSINESS_ERROR;
        this.level = ErrorLevel.ERROR;
    }


    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public ErrorType getType() {
        return type;
    }

    public ErrorLevel getLevel() {
        return level;
    }

    public static ErrorCode getByCode(String code) {
        ErrorCode[] errors = ErrorCode.values();
        for (ErrorCode ec : errors) {
            if (ec.getCode().equals(code)) {
                return ec;
            }
        }
        log.warn("code not found while invoking getByCode: {}", code);
        return UNKNOWN_ERROR;
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
