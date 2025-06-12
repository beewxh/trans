package com.hsbc.trans.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    TRANSFER("Transfer");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }
} 