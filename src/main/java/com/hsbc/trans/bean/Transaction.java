package com.hsbc.trans.bean;

import com.hsbc.trans.enums.TransactionStatus;
import com.hsbc.trans.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Transaction Entity Class
 * Represents a transaction record in the system, containing all transaction attributes and validation rules
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Data
@NoArgsConstructor
public class Transaction {
    /**
     * Transaction record ID, automatically generated unique identifier
     */
    @NotNull
    private Long id;

    /**
     * Business transaction ID, unique identifier provided by the business system
     */
    @NotEmpty
    private String transId;

    /**
     * User ID, identifies the user who initiated the transaction
     */
    @NotEmpty
    private String userId;

    /**
     * Transaction amount, must be greater than 0.01
     */
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    /**
     * Transaction type, such as deposit, withdrawal, etc.
     */
    @NotNull
    private TransactionType type;

    /**
     * Transaction status, such as pending, processing, completed, etc.
     */
    @NotNull
    private TransactionStatus status;

    /**
     * Transaction creation time
     */
    @NotNull
    private Timestamp createTime;

    /**
     * Transaction last update time
     */
    @NotNull
    private Timestamp updateTime;

    /**
     * Transaction description, optional field
     */
    private String description;

    /**
     * Create a new transaction record
     *
     * @param id Transaction record ID
     * @param transId Business transaction ID
     * @param userId User ID
     * @param amount Transaction amount
     * @param description Transaction description
     * @param type Transaction type
     */
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