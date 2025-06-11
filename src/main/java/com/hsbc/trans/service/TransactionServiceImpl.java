package com.hsbc.trans.service;

import com.hsbc.trans.vo.PageRequest;
import com.hsbc.trans.vo.PageResult;
import com.hsbc.trans.bean.Transaction;
import com.hsbc.trans.dao.TransactionDao;
import com.hsbc.trans.enums.TransactionStatus;
import com.hsbc.trans.enums.TransactionType;
import com.hsbc.common.errorhandler.exception.BusinessException;
import com.hsbc.trans.enums.ErrorCode;
import com.hsbc.common.utils.SnowflakeIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "transactions", key = "#id")
    public Transaction getTransaction(Long id) {
        return transactionDao.queryById(id)
            .orElseThrow(() -> new BusinessException("Transaction not found with id: " + id).code(ErrorCode.TRANSACTION_NOT_FOUND.getCode()));
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionDao.queryList();
    }

    @Override
    public PageResult<Transaction> getTransactions(PageRequest pageRequest) {
        return transactionDao.queryPage(pageRequest);
    }

    @Override
    @CacheEvict(value = "transactions", key = "#id")
    public Transaction updateTransactionStatus(Long id, TransactionStatus status) {
        Transaction transaction = this.getTransaction(id);
        transaction.setStatus(status);
        return transactionDao.updateById(transaction);
    }

    @Override
    @CacheEvict(value = "transactions", key = "#id")
    public void deleteTransaction(Long id) {
        transactionDao.deleteById(id);
    }
} 