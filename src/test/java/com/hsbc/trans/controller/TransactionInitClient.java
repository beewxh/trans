package com.hsbc.trans.controller;

import com.hsbc.common.response.CommonResponse;
import com.hsbc.trans.enums.TransactionType;
import com.hsbc.common.util.JsonUtils;
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
 * Transaction Data Initialization Client
 * Used to generate test data by creating random transaction records through HTTP interface
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Slf4j
public class TransactionInitClient {
    private static final Random RANDOM = new Random();
    private static final List<TransactionType> TYPES = List.of(TransactionType.values());

    /**
     * Main method
     * Create 10 random transaction records for testing
     *
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        // 1. Create HttpClient
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

        // 2. Create request
        List<CompletableFuture<HttpResponse<String>>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            // Build request data
            TransactionReq req = createRandomTransaction(i);

            // Build HTTP request
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

    /**
     * Create random transaction request object
     *
     * @param index Transaction sequence number, used to generate unique transaction ID
     * @return Randomly generated transaction request object
     */
    private static TransactionReq createRandomTransaction(int index) {
        TransactionReq req = new TransactionReq();
        req.setTransId(String.format("TX%d_%d", System.currentTimeMillis(), index));
        req.setUserId("USER" + String.format("%03d", RANDOM.nextInt(1000)));
        req.setAmount(randomAmount());
        req.setDescription(generateDescription(index));
        req.setType(randomTransactionType());
        return req;
    }

    /**
     * Generate random transaction amount
     * Generate amount between 100-10000 with 2 decimal places
     *
     * @return Randomly generated transaction amount
     */
    private static BigDecimal randomAmount() {
        // Generate random amount between 100-10000 with 2 decimal places
        return BigDecimal.valueOf(RANDOM.nextDouble() * 9900 + 100)
            .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Randomly select transaction type
     *
     * @return Randomly selected transaction type
     */
    private static TransactionType randomTransactionType() {
        return TYPES.get(RANDOM.nextInt(TYPES.size()));
    }

    /**
     * Generate transaction description
     *
     * @param index Transaction sequence number
     * @return Generated transaction description
     */
    private static String generateDescription(int index) {
        return String.format("Test Transaction-%d-%s", index + 1,
            randomTransactionType().getDescription());
    }

}