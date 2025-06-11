package com.hsbc.trans.service;

import com.hsbc.common.errorhandler.exception.BusinessException;
import com.hsbc.common.util.SnowflakeIdGenerator;
import com.hsbc.trans.bean.Transaction;
import com.hsbc.trans.dao.TransactionDao;
import com.hsbc.trans.enums.ErrorCode;
import com.hsbc.trans.enums.TransactionStatus;
import com.hsbc.trans.enums.TransactionType;
import com.hsbc.trans.vo.PageRequest;
import com.hsbc.trans.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 交易服务实现类
 * 实现了{@link TransactionService}接口，提供交易相关的业务操作实现
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    /**
     * 交易数据访问对象
     */
    private final TransactionDao transactionDao;

    /**
     * ID生成器
     */
    private final SnowflakeIdGenerator idGenerator;

    /**
     * 构造函数
     *
     * @param transactionDao 交易数据访问对象
     * @param idGenerator ID生成器
     */
    @Autowired
    public TransactionServiceImpl(TransactionDao transactionDao, SnowflakeIdGenerator idGenerator) {
        this.transactionDao = transactionDao;
        this.idGenerator = idGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Transaction createTransaction(String transId, String userId, BigDecimal amount, String description, TransactionType type) {
        Transaction transaction = new Transaction(idGenerator.nextId(), transId, userId, amount, description, type);
        return transactionDao.add(transaction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Transaction getTransaction(Long id) {
        return transactionDao.queryById(id)
            .orElseThrow(() -> new BusinessException("交易记录未找到，ID: " + id).code(ErrorCode.TRANSACTION_NOT_FOUND.getCode()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionDao.queryList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResult<Transaction> getTransactionPage(PageRequest pageRequest) {
        return transactionDao.queryPage(pageRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Transaction updateTransactionStatus(Long id, TransactionStatus status, String description) {
        Transaction transaction = this.getTransaction(id);
        if (!TransactionStatus.canTransit(transaction.getStatus(), status)) {
            throw new BusinessException("交易状态变更不合法: " + transaction.getStatus() + " -> " + status)
                .code(ErrorCode.TRANSACTION_UPDATE_STATUS_INVALID.getCode());
        }
        transaction.setStatus(status);
        transaction.setDescription(description);
        return transactionDao.updateById(transaction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Transaction deleteTransaction(Long id) {
        return transactionDao.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Transaction getTransactionByTransId(String transId) {
        return transactionDao.queryByTransId(transId)
            .orElseThrow(() -> new BusinessException("交易记录未找到，业务ID: " + transId).code(ErrorCode.TRANSACTION_NOT_FOUND.getCode()));
    }
} 