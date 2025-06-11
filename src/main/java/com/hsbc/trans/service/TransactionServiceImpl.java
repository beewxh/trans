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

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionDao transactionDao;
    private final SnowflakeIdGenerator idGenerator;


    @Autowired
    public TransactionServiceImpl(TransactionDao transactionDao, SnowflakeIdGenerator idGenerator) {
        this.transactionDao = transactionDao;
        this.idGenerator = idGenerator;
    }

    @Override
    public Transaction createTransaction(String transId, String userId, BigDecimal amount, String description, TransactionType type) {
        Transaction transaction = new Transaction(idGenerator.nextId(), transId, userId, amount, description, type);
        return transactionDao.add(transaction);
    }

    @Override
    public Transaction getTransaction(Long id) {
        return transactionDao.queryById(id)
            .orElseThrow(() -> new BusinessException("Transaction not found with id: " + id).code(ErrorCode.TRANSACTION_NOT_FOUND.getCode()));
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionDao.queryList();
    }

    @Override
    public PageResult<Transaction> getTransactionPage(PageRequest pageRequest) {
        return transactionDao.queryPage(pageRequest);
    }

    @Override
    public Transaction updateTransactionStatus(Long id, TransactionStatus status, String description) {
        Transaction transaction = this.getTransaction(id);
        if (!TransactionStatus.canTransit(transaction.getStatus(), status)) {
            throw new BusinessException("Transaction can't be updated, due to invalid status transition: " + transaction.getStatus() + " -> " + status)
                .code(ErrorCode.TRANSACTION_UPDATE_STATUS_INVALID.getCode());
        }
        transaction.setStatus(status);
        transaction.setDescription(description);
        return transactionDao.updateById(transaction);
    }

    @Override
    public Transaction deleteTransaction(Long id) {
        return transactionDao.deleteById(id);
    }

    @Override
    public Transaction getTransactionByTransId(String transId) {
        return transactionDao.queryByTransId(transId)
            .orElseThrow(() -> new BusinessException("Transaction not found with transId: " + transId).code(ErrorCode.TRANSACTION_NOT_FOUND.getCode()));
    }
} 