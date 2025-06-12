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
 * 交易内部控制器
 * 提供内部数据管理功能，主要用于测试环境的数据维护
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
     * 交易服务
     */
    private final TransactionService transactionService;

    /**
     * 构造函数
     *
     * @param transactionService 交易服务
     */
    @Autowired
    public TransactionInnerController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * 清空所有交易数据
     * 此接口仅用于测试环境，用于清理测试数据
     *
     * @return ResponseEntity 包含清理操作的响应结果
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