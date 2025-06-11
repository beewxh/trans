package com.hsbc.trans.service;

import com.hsbc.trans.vo.PageRequest;
import com.hsbc.trans.vo.PageResult;
import com.hsbc.trans.bean.Transaction;
import com.hsbc.trans.enums.TransactionStatus;
import com.hsbc.trans.enums.TransactionType;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    Transaction createTransaction(String transId, String userId, BigDecimal amount, String description, TransactionType type);

    Transaction getTransaction(Long id);

    List<Transaction> getAllTransactions();

    PageResult<Transaction> getTransactionPage(PageRequest pageRequest);

    Transaction updateTransactionStatus(Long id, TransactionStatus status, String description);

    Transaction deleteTransaction(Long id);

    Transaction getTransactionByTransId(@Positive String transId);
} 