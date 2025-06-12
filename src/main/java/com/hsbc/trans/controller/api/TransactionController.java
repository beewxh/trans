package com.hsbc.trans.controller.api;

import com.hsbc.common.response.CommonResponse;
import com.hsbc.common.validation.EnumValue;
import com.hsbc.common.validation.ValidationUtils;
import com.hsbc.trans.bean.Transaction;
import com.hsbc.trans.enums.TransactionStatus;
import com.hsbc.trans.service.TransactionService;
import com.hsbc.trans.vo.PageRequest;
import com.hsbc.trans.vo.PageResult;
import com.hsbc.trans.vo.TransactionReq;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Transaction Controller
 * Provides REST API endpoints for transaction-related operations, including create, query, update, and delete
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Slf4j
@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

    /**
     * Transaction service
     */
    private final TransactionService transactionService;

    /**
     * Parameter validation utility
     */
    private final ValidationUtils validationUtils;

    /**
     * Constructor
     *
     * @param transactionService Transaction service
     * @param validationUtils Parameter validation utility
     */
    @Autowired
    public TransactionController(TransactionService transactionService, ValidationUtils validationUtils) {
        this.transactionService = transactionService;
        this.validationUtils = validationUtils;
    }

    /**
     * Create a new transaction record
     *
     * @param req Transaction creation request
     * @return Created transaction record
     */
    @PostMapping("/create")
    public ResponseEntity<CommonResponse<Transaction>> createTransaction(
        @RequestBody TransactionReq req) {
        log.info("Start creating transaction, request parameters: {}", req);
        validationUtils.validateParams(req);
        Transaction transaction = transactionService.createTransaction(
            req.getTransId(), req.getUserId(), req.getAmount(), req.getDescription(), req.getType());
        log.info("Transaction created successfully, ID: {}, Business ID: {}", transaction.getId(), transaction.getTransId());
        return ResponseEntity.ok(CommonResponse.succeed(transaction));
    }

    /**
     * Query transaction record by ID
     *
     * @param id Transaction record ID
     * @return Transaction record
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<Transaction>> getTransaction(@PathVariable @Positive String id) {
        log.info("Start querying transaction, ID: {}", id);
        Transaction transaction = transactionService.getTransaction(Long.valueOf(id));
        log.info("Transaction query successful, details: {}", transaction);
        return ResponseEntity.ok(CommonResponse.succeed(transaction));
    }

    /**
     * Query transaction records with pagination
     *
     * @param page Page number, starting from 0
     * @param size Records per page
     * @return Paginated result
     */
    @GetMapping("/page")
    public ResponseEntity<CommonResponse<PageResult<Transaction>>> getTransactionPage(
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Start querying transactions by page, page number: {}, page size: {}", page, size);
        PageResult<Transaction> result = transactionService.getTransactionPage(new PageRequest(page, size));
        log.info("Transaction page query successful, total records: {}, current page records: {}", result.getTotalElements(), result.getContent().size());
        return ResponseEntity.ok(CommonResponse.succeed(result));
    }

    /**
     * Query all transaction records
     *
     * @return List of transaction records
     */
    @GetMapping("/all")
    public ResponseEntity<CommonResponse<List<Transaction>>> getAllTransactions() {
        log.info("Start querying all transactions");
        List<Transaction> transactions = transactionService.getAllTransactions();
        log.info("Query all transactions successful, total records: {}", transactions.size());
        return ResponseEntity.ok(CommonResponse.succeed(transactions));
    }

    /**
     * Update transaction status
     *
     * @param id Transaction record ID
     * @param status New transaction status
     * @param description Update description
     * @return Updated transaction record
     */
    @GetMapping("/{id}/update")
    public ResponseEntity<CommonResponse<Transaction>> updateTransactionStatus(
        @PathVariable @Positive String id,
        @RequestParam @NotNull @EnumValue(enumClass = TransactionStatus.class, message = "Invalid transaction status value") String status,
        @RequestParam String description
    ) {
        log.info("Start updating transaction status, ID: {}, new status: {}, description: {}", id, status, description);
        Transaction transaction = transactionService.updateTransactionStatus(
            Long.valueOf(id), TransactionStatus.valueOf(status), description);
        log.info("Transaction status update successful, ID: {}, new status: {}", transaction.getId(), transaction.getStatus());
        return ResponseEntity.ok(CommonResponse.succeed(transaction));
    }

    /**
     * Delete transaction record
     *
     * @param id Transaction record ID
     * @return No content response
     */
    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.info("Start deleting transaction, ID: {}", id);
        transactionService.deleteTransaction(id);
        log.info("Transaction deletion successful, ID: {}", id);
        return ResponseEntity.ok().build();
    }

    /**
     * Query transaction record by business transaction ID
     *
     * @param transId Business transaction ID
     * @return Transaction record
     */
    @GetMapping("/trans/{transId}")
    public ResponseEntity<CommonResponse<Transaction>> getTransactionByTransId(@PathVariable String transId) {
        log.info("Start querying transaction by business ID: {}", transId);
        Transaction transaction = transactionService.getTransactionByTransId(transId);
        log.info("Query transaction by business ID successful, details: {}", transaction);
        return ResponseEntity.ok(CommonResponse.succeed(transaction));
    }
} 