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
 * Transaction Service Interface
 * Defines business operations related to transactions, including create, query, update, and delete functionality
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
public interface TransactionService {

    /**
     * Create a new transaction record
     *
     * @param transId Business transaction ID
     * @param userId User ID
     * @param amount Transaction amount
     * @param description Transaction description
     * @param type Transaction type
     * @return Created transaction record
     * @throws com.hsbc.common.errorhandler.exception.BusinessException if transaction record already exists
     */
    Transaction createTransaction(String transId, String userId, BigDecimal amount, String description, TransactionType type);

    /**
     * Query transaction record by ID
     *
     * @param id Transaction record ID
     * @return Transaction record
     * @throws com.hsbc.common.errorhandler.exception.BusinessException if transaction record not found
     */
    Transaction getTransaction(Long id);

    /**
     * Query all transaction records
     *
     * @return List of transaction records
     */
    List<Transaction> getAllTransactions();

    /**
     * Query transaction records with pagination
     *
     * @param pageRequest Pagination request parameters
     * @return Paginated result
     */
    PageResult<Transaction> getTransactionPage(PageRequest pageRequest);

    /**
     * Update transaction status
     *
     * @param id Transaction record ID
     * @param status New transaction status
     * @param description Update description
     * @return Updated transaction record
     * @throws com.hsbc.common.errorhandler.exception.BusinessException if transaction record not found or status transition is invalid
     */
    Transaction updateTransactionStatus(Long id, TransactionStatus status, String description);

    /**
     * Delete transaction record
     *
     * @param id Transaction record ID
     * @return Deleted transaction record
     * @throws com.hsbc.common.errorhandler.exception.BusinessException if transaction record not found
     */
    Transaction deleteTransaction(Long id);

    /**
     * Query transaction record by business transaction ID
     *
     * @param transId Business transaction ID
     * @return Transaction record
     * @throws com.hsbc.common.errorhandler.exception.BusinessException if transaction record not found
     */
    Transaction getTransactionByTransId(@Positive String transId);
} 