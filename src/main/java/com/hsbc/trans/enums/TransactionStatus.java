package com.hsbc.trans.enums;

import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
public enum TransactionStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }


    private static final Map<TransactionStatus, Set<TransactionStatus>> statusTransitionMap
        = Map.of(PENDING, Set.of(PROCESSING, CANCELLED), PROCESSING, Set.of(COMPLETED, FAILED), COMPLETED, Set.of(), FAILED, Set.of());

    public static boolean canTransit(TransactionStatus from, TransactionStatus to) {
        return statusTransitionMap.get(from).contains(to);
    }

} 