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
 * 交易创建请求对象
 * 用于接收创建交易的请求参数，包含必要的参数验证规则
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Data
public class TransactionReq {

    /**
     * 业务交易ID，必须唯一
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
     * 交易类型，必须是TransactionType枚举中的有效值
     */
    @NotNull
    @EnumValue(enumClass = TransactionType.class, message = "交易类型枚举值错误")
    private TransactionType type;

    /**
     * 交易描述，可选字段
     */
    private String description;
}
