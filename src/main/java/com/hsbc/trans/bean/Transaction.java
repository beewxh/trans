package com.hsbc.trans.bean;

import com.hsbc.trans.enums.TransactionStatus;
import com.hsbc.trans.enums.TransactionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class Transaction {
    // 主键
    @NotNull
    private Long id;
    // 业务订单ID
    @NotEmpty
    private String transId;

    @NotEmpty
    private String userId;

    @NotNull
    @PositiveOrZero
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    @NotNull
    private TransactionStatus status;

    @NotNull
    private Timestamp createTime;
    @NotNull
    private Timestamp updateTime;

    private String description;


    public Transaction(Long id, String transId, String userId, BigDecimal amount, String description, TransactionType type) {
        this.id = id;
        this.transId = transId;
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.updateTime = new Timestamp(System.currentTimeMillis());
        this.status = TransactionStatus.PENDING;
    }
} 