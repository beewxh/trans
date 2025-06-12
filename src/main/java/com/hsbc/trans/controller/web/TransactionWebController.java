package com.hsbc.trans.controller.web;

import com.hsbc.common.response.CommonResponse;
import com.hsbc.trans.bean.Transaction;
import com.hsbc.trans.enums.TransactionStatus;
import com.hsbc.trans.enums.TransactionType;
import com.hsbc.trans.service.TransactionService;
import com.hsbc.trans.vo.PageRequest;
import com.hsbc.trans.vo.PageResult;
import com.hsbc.trans.vo.TransactionReq;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Transaction Web Controller
 * Provides web page endpoints for transaction management
 */
@Slf4j
@WebController
@RequestMapping("/web/transactions")
public class TransactionWebController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionWebController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public String listTransactions(
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size,
            Model model) {
        PageResult<Transaction> result = transactionService.getTransactionPage(new PageRequest(page, size));
        model.addAttribute("transactions", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (result.getTotalElements() + size - 1) / size);
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("transactionStatuses", TransactionStatus.values());
        return "transaction/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("transactionTypes", TransactionType.values());
        return "transaction/create";
    }

    @PostMapping("/create")
    public String createTransaction(@ModelAttribute TransactionReq req) {
        transactionService.createTransaction(
                req.getTransId(),
                req.getUserId(),
                req.getAmount(),
                req.getDescription(),
                req.getType()
        );
        return "redirect:/web/transactions";
    }

    @PostMapping("/{id}/delete")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return "redirect:/web/transactions";
    }

    @PostMapping(value = "/{id}/update-status", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CommonResponse<Transaction> updateTransactionStatus(
            @PathVariable Long id,
            @RequestParam TransactionStatus status,
            @RequestParam(required = false) String description) {
        Transaction transaction = transactionService.updateTransactionStatus(id, status, description);
        return CommonResponse.succeed(transaction);
    }
}
