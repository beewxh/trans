package com.hsbc.trans.enums;

import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
public enum TransactionStatus {
    PENDING("待处理"),
    PROCESSING("处理中"),
    COMPLETED("已完成"),
    FAILED("失败"),
    CANCELLED("已取消");

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