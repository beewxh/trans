package com.hsbc.trans.enums;

import com.hsbc.common.errorhandler.enums.ErrorLevel;
import com.hsbc.common.errorhandler.enums.ErrorType;
import com.hsbc.common.util.JsonUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 错误码枚举
 * 定义系统中所有的错误码，包括系统错误和业务错误
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Getter
@Slf4j
public enum ErrorCode {

    /**
     * 系统错误，表示系统内部未知异常
     */
    SYSTEM_ERROR("000001", "系统异常，请稍后再试", ErrorType.SYSTEM_ERROR, ErrorLevel.ERROR),

    /**
     * 参数错误，表示接口参数验证失败
     */
    PARAM_ERROR("000002", "参数错误", ErrorType.PARAM_ERROR, ErrorLevel.INFO),

    /**
     * 未知错误，表示未定义的错误类型
     */
    UNKNOWN_ERROR("000003", "未知错误", ErrorType.SYSTEM_ERROR, ErrorLevel.ERROR),

    /**
     * 交易订单未找到错误
     */
    TRANSACTION_NOT_FOUND("100001", "交易订单未找到"),

    /**
     * 交易订单重复错误
     */
    TRANSACTION_DUPLICATE("100002", "交易订单已存在", ErrorType.BUSINESS_ERROR, ErrorLevel.WARN),

    /**
     * 交易订单未变更错误
     */
    TRANSACTION_NOT_CHANGED("100003", "更新操作时交易订单没有变更", ErrorType.BUSINESS_ERROR, ErrorLevel.WARN),

    /**
     * 交易状态变更非法错误
     */
    TRANSACTION_UPDATE_STATUS_INVALID("100004", "更新操作时状态变迁不合法");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误消息
     */
    private final String msg;

    /**
     * 错误类型
     */
    private final ErrorType type;

    /**
     * 错误级别
     */
    private final ErrorLevel level;

    /**
     * 构造函数
     *
     * @param code 错误码
     * @param msg 错误消息
     * @param type 错误类型
     * @param level 错误级别
     */
    ErrorCode(String code, String msg, ErrorType type, ErrorLevel level) {
        this.msg = msg;
        this.code = code;
        this.type = type;
        this.level = level;
    }

    /**
     * 构造函数，默认使用业务错误类型和错误级别
     *
     * @param code 错误码
     * @param msg 错误消息
     */
    ErrorCode(String code, String msg) {
        this.msg = msg;
        this.code = code;
        this.type = ErrorType.BUSINESS_ERROR;
        this.level = ErrorLevel.ERROR;
    }

    /**
     * 根据错误码获取对应的错误码枚举实例
     *
     * @param code 错误码
     * @return 错误码枚举实例，如果未找到则返回UNKNOWN_ERROR
     */
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

    /**
     * 转换为JSON字符串
     *
     * @return JSON格式的错误码信息
     */
    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
