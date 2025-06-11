package com.hsbc.common.errorhandler.enums;

public enum ErrorLevel {

    INFO(1),

    WARN(2),

    ERROR(3);

    private Integer code;

    ErrorLevel(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static ErrorLevel getByCode(int code) {
        ErrorLevel[] errors = ErrorLevel.values();
        for (ErrorLevel level : errors) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return ERROR;
    }

}
