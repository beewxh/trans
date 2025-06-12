package com.hsbc.trans.vo;

import com.hsbc.common.validation.EnumValue;
import com.hsbc.trans.enums.TransactionStatus;
import com.hsbc.trans.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Transaction Request Object
 * Used to encapsulate transaction creation request parameters
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Data
public class TransactionReq {

    /**
     * Business transaction ID, must be unique
     */
    @NotEmpty
    private String transId;

    /**
     * User ID who initiated the transaction
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
     * Transaction type
     */
    @NotNull
    @EnumValue(enumClass = TransactionType.class, message = "Invalid transaction type value")
    private TransactionType type;

    /**
     * Transaction description (optional)
     */
    private String description;
}
