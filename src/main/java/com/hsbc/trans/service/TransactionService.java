package com.hsbc.trans.service;

import com.hsbc.trans.vo.PageRequest;
import com.hsbc.trans.vo.PageResult;
import com.hsbc.trans.bean.Transaction;
import com.hsbc.trans.enums.TransactionStatus;
import com.hsbc.trans.enums.TransactionType;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/**
 * 交易服务接口
 * 定义了交易相关的业务操作，包括创建、查询、更新和删除交易等功能
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public interface TransactionService {

    /**
     * 创建新的交易记录
     *
     * @param transId 业务交易ID
     * @param userId 用户ID
     * @param amount 交易金额
     * @param description 交易描述
     * @param type 交易类型
     * @return 创建成功的交易记录
     * @throws com.hsbc.common.errorhandler.exception.BusinessException 如果交易记录已存在
     */
    Transaction createTransaction(String transId, String userId, BigDecimal amount, String description, TransactionType type);

    /**
     * 根据ID查询交易记录
     *
     * @param id 交易记录ID
     * @return 交易记录
     * @throws com.hsbc.common.errorhandler.exception.BusinessException 如果交易记录不存在
     */
    Transaction getTransaction(Long id);

    /**
     * 查询所有交易记录
     *
     * @return 交易记录列表
     */
    List<Transaction> getAllTransactions();

    /**
     * 分页查询交易记录
     *
     * @param pageRequest 分页请求参数
     * @return 分页结果
     */
    PageResult<Transaction> getTransactionPage(PageRequest pageRequest);

    /**
     * 更新交易状态
     *
     * @param id 交易记录ID
     * @param status 新的交易状态
     * @param description 更新说明
     * @return 更新后的交易记录
     * @throws com.hsbc.common.errorhandler.exception.BusinessException 如果交易记录不存在或状态变更不合法
     */
    Transaction updateTransactionStatus(Long id, TransactionStatus status, String description);

    /**
     * 删除交易记录
     *
     * @param id 交易记录ID
     * @return 被删除的交易记录
     * @throws com.hsbc.common.errorhandler.exception.BusinessException 如果交易记录不存在
     */
    Transaction deleteTransaction(Long id);

    /**
     * 根据业务交易ID查询交易记录
     *
     * @param transId 业务交易ID
     * @return 交易记录
     * @throws com.hsbc.common.errorhandler.exception.BusinessException 如果交易记录不存在
     */
    Transaction getTransactionByTransId(@Positive String transId);
} 