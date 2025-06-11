package com.hsbc.trans.controller;

import com.hsbc.common.response.CommonResponse;
import com.hsbc.trans.enums.TransactionType;
import com.hsbc.common.utils.JsonUtils;
import com.hsbc.trans.vo.TransactionReq;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * @ClassName TransactionInitClient
 * @Description TODO
 * @Author rd
 * @Date 2025/6/11 21:22
 * @Version 1.0
 **/
@Slf4j
public class TransactionInitClient {
    private static final Random RANDOM = new Random();
    private static final List<TransactionType> TYPES = List.of(TransactionType.values());

    public static void main(String[] args) {
        // 1. 创建HttpClient
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

        // 2. 创建请求
        List<CompletableFuture<HttpResponse<String>>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            // 构建请求数据
            TransactionReq req = createRandomTransaction(i);

            // 构建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/transactions/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(req)))
                .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                log.info("Transaction {}} - Status: {}", i + 1, response.statusCode());
                if (response.statusCode() == 200) {
                    var commonResponse = JsonUtils.toBean(response.body(), CommonResponse.class);
                    log.info("Transaction {} - Response Code: {}", i + 1, commonResponse.getCode());
                    log.info("Transaction {} - Response Body: {}", i + 1, response.body());
                } else {
                    log.error("Transaction {} failed with status: {}}", i + 1, response.statusCode());
                }
                Thread.sleep(100);
            } catch (Exception e) {
                log.error("Transaction {} failed: {}", i + 1, e.getMessage());
            }
        }

    }

    private static TransactionReq createRandomTransaction(int index) {
        TransactionReq req = new TransactionReq();
        req.setTransId(String.format("TX%d_%d", System.currentTimeMillis(), index));
        req.setUserId("USER" + String.format("%03d", RANDOM.nextInt(1000)));
        req.setAmount(randomAmount());
        req.setDescription(generateDescription(index));
        req.setType(randomTransactionType());
        return req;
    }

    private static BigDecimal randomAmount() {
        // 生成100-10000之间的随机金额，保留2位小数
        return BigDecimal.valueOf(RANDOM.nextDouble() * 9900 + 100)
            .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private static TransactionType randomTransactionType() {
        return TYPES.get(RANDOM.nextInt(TYPES.size()));
    }

    private static String generateDescription(int index) {
        return String.format("测试交易-%d-%s", index + 1,
            randomTransactionType().getDescription());
    }

}