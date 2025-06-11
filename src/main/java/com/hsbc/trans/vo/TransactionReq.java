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
 * @ClassName TransactionReq
 * @Description TODO
 * @Author rd
 * @Date 2025/6/11 21:08
 * @Version 1.0
 **/
@Data
public class TransactionReq {

    @NotEmpty
    private String transId;

    @NotEmpty
    private String userId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    @EnumValue(enumClass = TransactionType.class, message = "交易类型枚举值错误")
    private TransactionType type;

    private String description;
}
