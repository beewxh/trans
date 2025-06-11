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
 * 交易控制器
 * 提供交易相关的REST API接口，包括创建、查询、更新和删除交易等功能
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
     * 交易服务
     */
    private final TransactionService transactionService;

    /**
     * 参数验证工具
     */
    private final ValidationUtils validationUtils;

    /**
     * 构造函数
     *
     * @param transactionService 交易服务
     * @param validationUtils 参数验证工具
     */
    @Autowired
    public TransactionController(TransactionService transactionService, ValidationUtils validationUtils) {
        this.transactionService = transactionService;
        this.validationUtils = validationUtils;
    }

    /**
     * 创建新的交易记录
     *
     * @param req 交易创建请求
     * @return 创建成功的交易记录
     */
    @PostMapping("/create")
    public ResponseEntity<CommonResponse<Transaction>> createTransaction(
        @RequestBody TransactionReq req) {
        log.info("开始创建交易，请求参数：{}", req);
        validationUtils.validateParams(req);
        Transaction transaction = transactionService.createTransaction(
            req.getTransId(), req.getUserId(), req.getAmount(), req.getDescription(), req.getType());
        log.info("交易创建成功，交易ID：{}，业务交易ID：{}", transaction.getId(), transaction.getTransId());
        return ResponseEntity.ok(CommonResponse.succeed(transaction));
    }

    /**
     * 根据ID查询交易记录
     *
     * @param id 交易记录ID
     * @return 交易记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<Transaction>> getTransaction(@PathVariable @Positive String id) {
        log.info("开始查询交易，交易ID：{}", id);
        Transaction transaction = transactionService.getTransaction(Long.valueOf(id));
        log.info("交易查询成功，交易详情：{}", transaction);
        return ResponseEntity.ok(CommonResponse.succeed(transaction));
    }

    /**
     * 分页查询交易记录
     *
     * @param page 页码，从0开始
     * @param size 每页记录数
     * @return 分页结果
     */
    @GetMapping("/page")
    public ResponseEntity<CommonResponse<PageResult<Transaction>>> getTransactionPage(
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("开始分页查询交易，页码：{}，每页记录数：{}", page, size);
        PageResult<Transaction> result = transactionService.getTransactionPage(new PageRequest(page, size));
        log.info("交易分页查询成功，总记录数：{}，当前页记录数：{}", result.getTotalElements(), result.getContent().size());
        return ResponseEntity.ok(CommonResponse.succeed(result));
    }

    /**
     * 查询所有交易记录
     *
     * @return 交易记录列表
     */
    @GetMapping("/all")
    public ResponseEntity<CommonResponse<List<Transaction>>> getAllTransactions() {
        log.info("开始查询所有交易");
        List<Transaction> transactions = transactionService.getAllTransactions();
        log.info("查询所有交易成功，总记录数：{}", transactions.size());
        return ResponseEntity.ok(CommonResponse.succeed(transactions));
    }

    /**
     * 更新交易状态
     *
     * @param id 交易记录ID
     * @param status 新的交易状态
     * @param description 更新说明
     * @return 更新后的交易记录
     */
    @GetMapping("/{id}/update")
    public ResponseEntity<CommonResponse<Transaction>> updateTransactionStatus(
        @PathVariable @Positive String id,
        @RequestParam @NotNull @EnumValue(enumClass = TransactionStatus.class, message = "交易状态枚举值错误") String status,
        @RequestParam String description
    ) {
        log.info("开始更新交易状态，交易ID：{}，新状态：{}，更新说明：{}", id, status, description);
        Transaction transaction = transactionService.updateTransactionStatus(
            Long.valueOf(id), TransactionStatus.valueOf(status), description);
        log.info("交易状态更新成功，交易ID：{}，新状态：{}", transaction.getId(), transaction.getStatus());
        return ResponseEntity.ok(CommonResponse.succeed(transaction));
    }

    /**
     * 删除交易记录
     *
     * @param id 交易记录ID
     * @return 无内容响应
     */
    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.info("开始删除交易，交易ID：{}", id);
        transactionService.deleteTransaction(id);
        log.info("交易删除成功，交易ID：{}", id);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据业务交易ID查询交易记录
     *
     * @param transId 业务交易ID
     * @return 交易记录
     */
    @GetMapping("/trans/{transId}")
    public ResponseEntity<CommonResponse<Transaction>> getTransactionByTransId(@PathVariable String transId) {
        log.info("开始根据业务交易ID查询交易，业务交易ID：{}", transId);
        Transaction transaction = transactionService.getTransactionByTransId(transId);
        log.info("根据业务交易ID查询交易成功，交易详情：{}", transaction);
        return ResponseEntity.ok(CommonResponse.succeed(transaction));
    }
} 