package com.hsbc.trans.controller.inner;

import com.hsbc.common.response.CommonResponse;
import com.hsbc.trans.bean.Transaction;
import com.hsbc.trans.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Transaction Internal Controller
 * Provides internal data management functionality, primarily used for data maintenance in test environments
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Slf4j
@RestController
@RequestMapping("/inner/transactions")
public class TransactionInnerController {

    /**
     * Transaction service
     */
    private final TransactionService transactionService;

    /**
     * Constructor
     *
     * @param transactionService Transaction service
     */
    @Autowired
    public TransactionInnerController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Clears all transaction data
     * This endpoint is only used in test environments for cleaning up test data
     *
     * @return ResponseEntity containing the response of the cleanup operation
     */
    @PostMapping("/clear")
    public ResponseEntity<CommonResponse<Void>> clearAllTransactions() {
        log.info("Start clearing all transaction data");
        List<Transaction> allTransactions = transactionService.getAllTransactions();
        
        int count = 0;
        for (Transaction transaction : allTransactions) {
            transactionService.deleteTransaction(transaction.getId());
            log.info("Transaction record deleted, ID: {}, details: {}", transaction.getId(), transaction);
            count++;
        }
        
        log.info("Transaction data cleanup completed, {} records cleared", count);
        return ResponseEntity.ok(CommonResponse.succeed(null));
    }
} 