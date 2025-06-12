package com.hsbc.trans.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.hsbc.common.response.enums.ResponseCode;
import com.hsbc.common.util.JsonUtils;
import com.hsbc.trans.bean.Transaction;
import static com.hsbc.trans.enums.ErrorCode.TRANSACTION_NOT_FOUND;
import com.hsbc.trans.enums.TransactionStatus;
import com.hsbc.trans.enums.TransactionType;
import com.hsbc.trans.vo.TransactionReq;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Transaction Concurrency Test Client
 * Tests the behavior of the transaction system in concurrent scenarios,
 * including concurrent updates and delete operations
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Slf4j
public class TransactionSyncTestClient {

    /**
     * Test server address
     */
    private static final String BASE_URL = "http://localhost:8080";

    /**
     * HTTP client
     */
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    /**
     * Thread pool
     */
    private ExecutorService executorService;

    /**
     * Setup before tests
     * Initializes the thread pool
     */
    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * Cleanup after tests
     * Shuts down the thread pool and cleans up test data
     */
    @AfterEach
    void tearDown() throws Exception {
        executorService.shutdown();
        if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
        clearTestData();
    }

    /**
     * Test concurrent transaction status updates
     * Multiple threads update the status of the same transaction simultaneously,
     * verifying the effectiveness of concurrency control
     */
    @Test
    void testConcurrentUpdateStatus() throws Exception {
        // Create test transaction
        Transaction transaction = createTestTransaction();
        assertNotNull(transaction);

        int threadCount = 5;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        List<Exception> exceptions = new ArrayList<>();

        // Start multiple threads for concurrent updates
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    updateTransactionStatus(transaction.getId(), 
                        index % 2 == 0 ? TransactionStatus.PROCESSING : TransactionStatus.COMPLETED);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Trigger concurrent updates
        startLatch.countDown();
        endLatch.await(30, TimeUnit.SECONDS);

        // Verify results
        assertTrue(exceptions.isEmpty(), "Exceptions occurred during concurrent updates");
        Transaction updatedTransaction = queryTransaction(transaction.getId());
        assertNotNull(updatedTransaction);
        assertNotEquals(TransactionStatus.PENDING, updatedTransaction.getStatus());
    }

    /**
     * Test concurrent transaction deletion
     * Multiple threads attempt to delete the same transaction simultaneously,
     * verifying the effectiveness of concurrency control
     */
    @Test
    void testConcurrentDelete() throws Exception {
        // Create test transaction
        Transaction transaction = createTestTransaction();
        assertNotNull(transaction);

        int threadCount = 5;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        List<Exception> exceptions = new ArrayList<>();

        // Start multiple threads for concurrent deletion
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    deleteTransaction(transaction.getId());
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Trigger concurrent deletion
        startLatch.countDown();
        endLatch.await(30, TimeUnit.SECONDS);

        // Verify results
        assertEquals(threadCount - 1, exceptions.size(), "Only one thread should succeed in deletion");
        Transaction deletedTransaction = queryTransaction(transaction.getId());
        assertNull(deletedTransaction);
    }

    /**
     * Creates a test transaction
     *
     * @return The created transaction record
     * @throws Exception if an error occurs during creation
     */
    private Transaction createTestTransaction() throws Exception {
        TransactionReq req = new TransactionReq();
        req.setTransId("TEST_" + System.currentTimeMillis());
        req.setUserId("TEST_USER");
        req.setAmount(new BigDecimal("100.00"));
        req.setType(TransactionType.DEPOSIT);
        req.setDescription("Test Transaction");

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/transactions"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(req)))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonNode jsonNode = JsonUtils.parseNode(response.body());
        assertEquals(ResponseCode.SUCC.getCode(), jsonNode.get("code").asText());

        return JsonUtils.fromJson(jsonNode.get("data").toString(), Transaction.class);
    }

    /**
     * Updates transaction status
     *
     * @param id Transaction ID
     * @param status New transaction status
     * @throws Exception if an error occurs during update
     */
    private void updateTransactionStatus(Long id, TransactionStatus status) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setStatus(status);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/transactions/" + id))
            .header("Content-Type", "application/json")
            .method("PATCH", HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(transaction)))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to update transaction status: " + response.body());
        }
    }

    /**
     * Deletes a transaction
     *
     * @param id Transaction ID to delete
     * @throws Exception if an error occurs during deletion
     */
    private void deleteTransaction(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/transactions/" + id))
            .DELETE()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to delete transaction: " + response.body());
        }
    }

    /**
     * Queries a transaction
     *
     * @param id Transaction ID to query
     * @return Transaction record, returns null if not found
     * @throws Exception if an error occurs during query
     */
    private Transaction queryTransaction(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/transactions/" + id))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 404) {
            return null;
        }

        JsonNode jsonNode = JsonUtils.parseNode(response.body());
        if (TRANSACTION_NOT_FOUND.getCode().equals(jsonNode.get("code").asText())) {
            return null;
        }

        return JsonUtils.fromJson(jsonNode.get("data").toString(), Transaction.class);
    }

    /**
     * Clears test data
     *
     * @throws Exception if an error occurs during clearing
     */
    private void clearTestData() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/inner/transactions/clear"))
            .DELETE()
            .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
