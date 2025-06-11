package com.hsbc.trans.controller;

import com.hsbc.common.response.CommonResponse;
import com.hsbc.trans.bean.PageRequest;
import com.hsbc.trans.bean.PageResult;
import com.hsbc.trans.bean.Transaction;
import com.hsbc.trans.enums.TransactionStatus;
import com.hsbc.trans.enums.TransactionType;
import com.hsbc.trans.service.TransactionService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<CommonResponse<Transaction>> createTransaction(
        @RequestParam @NotBlank String transId,
        @RequestParam @NotBlank String userId,
        @RequestParam @NotNull @DecimalMin("0.01") BigDecimal amount,
        @RequestParam @NotBlank String description,
        @RequestParam @NotNull TransactionType type) {
        return ResponseEntity.ok(CommonResponse.succeed(transactionService.createTransaction(transId, userId, amount, description, type)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<Transaction>> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(CommonResponse.succeed(transactionService.getTransaction(id)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResult<Transaction>>> getTransactions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(CommonResponse.succeed(transactionService.getTransactions(new PageRequest(page, size))));
    }

    @GetMapping("/all")
    public ResponseEntity<CommonResponse<List<Transaction>>> getAllTransactions() {
        return ResponseEntity.ok(CommonResponse.succeed(transactionService.getAllTransactions()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CommonResponse<Transaction>> updateTransactionStatus(
        @PathVariable Long id,
        @RequestParam @NotNull TransactionStatus status) {
        return ResponseEntity.ok(CommonResponse.succeed(transactionService.updateTransactionStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
} 