package com.hsbc.trans.enums;


import com.hsbc.common.errorhandler.enums.ErrorLevel;
import com.hsbc.common.errorhandler.enums.ErrorType;
import com.hsbc.common.util.JsonUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
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
    TRANSACTION_NOT_FOUND("100001", "交易订单未找到"),
    TRANSACTION_DUPLICATE("100002", "交易订单已存在", ErrorType.BUSINESS_ERROR, ErrorLevel.WARN),
    TRANSACTION_NOT_CHANGED("100003", "更新操作时交易订单没有变更", ErrorType.BUSINESS_ERROR, ErrorLevel.WARN),
    TRANSACTION_UPDATE_STATUS_INVALID("100004", "更新操作时状态变迁不合法"),
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
