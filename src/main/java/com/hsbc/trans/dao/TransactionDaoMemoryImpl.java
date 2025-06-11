package com.hsbc.trans.dao;

import com.hsbc.common.errorhandler.exception.BusinessException;
import com.hsbc.trans.bean.PageRequest;
import com.hsbc.trans.bean.PageResult;
import com.hsbc.trans.bean.Transaction;
import com.hsbc.trans.enums.ErrorCode;
import com.hsbc.trans.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@Repository
public class TransactionDaoMemoryImpl implements TransactionDao {

    private final Map<Long, Transaction> transactionStore = new ConcurrentSkipListMap<>();

    private final Map<String, Long> transIdIndexMap = new ConcurrentHashMap<>();

    @Autowired
    private ValidationUtils validationUtils;


    @Override
    public Transaction add(Transaction transaction) {

        validationUtils.validate(transaction);
        if (existsByTransId(transaction.getTransId())) {
            throw new RuntimeException(); //TODO
        }
        transIdIndexMap.put(transaction.getTransId(), transaction.getId());
        transactionStore.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public boolean existsByTransId(String transId) {
        return transIdIndexMap.containsKey(transId) && transactionStore.containsKey(transIdIndexMap.get(transId));
    }

    @Override
    public Optional<Transaction> queryById(Long id) {
        return Optional.ofNullable(transactionStore.get(id));
    }

    @Override
    public Optional<Transaction> queryByTransId(String transId) {
        return Optional.ofNullable(existsByTransId(transId) ? transactionStore.get(transIdIndexMap.get(transId)) : null);
    }

    @Override
    public List<Transaction> queryList() {
        return new ArrayList<>(transactionStore.values());
    }

    @Override
    public PageResult<Transaction> queryPage(PageRequest pageRequest) {

        List<Transaction> values = transactionStore.entrySet().stream()
            .skip(pageRequest.getOffset() - 1)
            .limit(pageRequest.getPageSize())
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        return new PageResult<>(values, transactionStore.size(), pageRequest);
    }


    @Override
    public Transaction updateById(Transaction transaction) {
        if (!existsById(transaction.getId())) {
            throw new BusinessException("Transaction not found with id: " + transaction.getId()).code(ErrorCode.TRANSACTION_NOT_FOUND.getCode());
        }
        Transaction origin = transactionStore.get(transaction.getId());
        synchronized (origin) {
            assign(origin, transaction);
            transactionStore.put(transaction.getId(), transaction);
        }
        return transaction;
    }

    private void assign(Transaction origin, Transaction changed) {
        origin.setStatus(changed.getStatus());
        origin.setDescription(changed.getDescription());
        origin.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    }


    @Override
    public void deleteById(Long id) {
        Transaction transaction = transactionStore.get(id);
        if (transaction == null) {
            throw new BusinessException("Transaction not found with id: " + id).code(ErrorCode.TRANSACTION_NOT_FOUND.getCode());
        }
        synchronized (transaction) {
            transactionStore.remove(id);
            transIdIndexMap.remove(transaction.getTransId());
        }
    }

    @Override
    public boolean existsById(Long id) {
        return transactionStore.containsKey(id);
    }
}