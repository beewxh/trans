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
 * @ClassName TransactionInnerController
 * @Description 交易内部控制器，内部数据管理
 * @Author rd
 * @Date 2025/6/12 06:00
 * @Version 1.0
 **/
@Slf4j
@RestController
@RequestMapping("/inner/transactions")
public class TransactionInnerController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionInnerController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * 清空所有交易数据
     * 此接口仅用于测试环境
     *
     * @return
     */
    @PostMapping("/clear")
    public ResponseEntity<CommonResponse<Void>> clearAllTransactions() {
        log.info("开始清空所有交易数据");
        List<Transaction> allTransactions = transactionService.getAllTransactions();

        int count = 0;
        for (Transaction transaction : allTransactions) {
            transactionService.deleteTransaction(transaction.getId());
            log.info("交易记录" + transaction.getId() + "被删除，" + transaction);
            count++;
        }

        log.info("交易数据清理完成，共清理{}条记录", count);
        return ResponseEntity.ok(CommonResponse.succeed(null));
    }
} 