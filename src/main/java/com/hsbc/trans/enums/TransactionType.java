package com.hsbc.trans.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    DEPOSIT("存款"),
    WITHDRAWAL("取款"),
    TRANSFER("转账");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }
} 