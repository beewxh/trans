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
 * 交易实体类
 * 用于表示系统中的交易记录，包含交易的所有属性和验证规则
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Data
@NoArgsConstructor
public class Transaction {
    /**
     * 交易记录ID，系统自动生成的唯一标识
     */
    @NotNull
    private Long id;

    /**
     * 业务交易ID，由业务系统提供的唯一标识
     */
    @NotEmpty
    private String transId;

    /**
     * 用户ID，标识交易的发起用户
     */
    @NotEmpty
    private String userId;

    /**
     * 交易金额，必须大于0.01
     */
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    /**
     * 交易类型，如存款、取款等
     */
    @NotNull
    private TransactionType type;

    /**
     * 交易状态，如待处理、处理中、已完成等
     */
    @NotNull
    private TransactionStatus status;

    /**
     * 交易创建时间
     */
    @NotNull
    private Timestamp createTime;

    /**
     * 交易最后更新时间
     */
    @NotNull
    private Timestamp updateTime;

    /**
     * 交易描述，可选字段
     */
    private String description;

    /**
     * 创建新的交易记录
     *
     * @param id 交易记录ID
     * @param transId 业务交易ID
     * @param userId 用户ID
     * @param amount 交易金额
     * @param description 交易描述
     * @param type 交易类型
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