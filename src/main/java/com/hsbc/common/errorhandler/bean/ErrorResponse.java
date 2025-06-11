package com.hsbc.common.errorhandler.bean;


import com.hsbc.trans.enums.ErrorCode;
import lombok.Data;

@Data
public class ErrorResponse {
    private String code;
    private String message;
    private String type;
    private int level;


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