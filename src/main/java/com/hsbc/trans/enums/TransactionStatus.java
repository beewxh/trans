package com.hsbc.trans.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TransactionStatus {
    PENDING("待处理"),
    COMPLETED("已完成"),
    FAILED("失败"),
    CANCELLED("已取消");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

} 