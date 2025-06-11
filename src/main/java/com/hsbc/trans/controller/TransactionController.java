package com.hsbc.trans.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    private final ValidationUtils validationUtils;

    @Autowired
    public TransactionController(TransactionService transactionService, ValidationUtils validationUtils) {
        this.transactionService = transactionService;
        this.validationUtils = validationUtils;
    }

    @PostMapping("/create")
    public ResponseEntity<CommonResponse<Transaction>> createTransaction(
        @RequestBody TransactionReq req) {
        validationUtils.validateParams(req);
        return ResponseEntity.ok(CommonResponse.succeed(
            transactionService.createTransaction(req.getTransId(), req.getUserId(), req.getAmount(), req.getDescription(), req.getType())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<Transaction>> getTransaction(@PathVariable @Positive String id) {
        return ResponseEntity.ok(CommonResponse.succeed(transactionService.getTransaction(Long.valueOf(id))));
    }

    @GetMapping("/page")
    public ResponseEntity<CommonResponse<PageResult<Transaction>>> getTransactions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(CommonResponse.succeed(transactionService.getTransactions(new PageRequest(page, size))));
    }

    @GetMapping("/all")
    public ResponseEntity<CommonResponse<List<Transaction>>> getAllTransactions() {
        return ResponseEntity.ok(CommonResponse.succeed(transactionService.getAllTransactions()));
    }

    @GetMapping("/{id}/status/update")
    public ResponseEntity<CommonResponse<Transaction>> updateTransactionStatus(
        @PathVariable @Positive String id,
        @RequestParam @NotNull @EnumValue(enumClass = TransactionStatus.class, message = "交易状态枚举值错误") String status) {
        return ResponseEntity.ok(CommonResponse.succeed(transactionService.updateTransactionStatus(Long.valueOf(id), TransactionStatus.valueOf(status))));
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
} 