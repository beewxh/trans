package com.hsbc.trans.controller;

import com.fasterxml.jackson.core.type.TypeReference;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * 交易并发测试客户端
 * 用于测试交易系统在并发场景下的行为，包括并发更新和删除操作
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Slf4j
public class TransactionSyncTestClient {

    /**
     * 测试服务器地址
     */
    private static final String BASE_URL = "http://localhost:8080";

    /**
     * HTTP客户端
     */
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    /**
     * 线程池
     */
    private ExecutorService executorService;

    /**
     * 测试前的准备工作
     * 初始化线程池
     */
    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * 测试后的清理工作
     * 关闭线程池并清理测试数据
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
     * 测试并发更新交易状态
     * 多个线程同时更新同一笔交易的状态，验证并发控制的有效性
     */
    @Test
    void testConcurrentUpdateStatus() throws Exception {
        // 创建测试交易
        Transaction transaction = createTestTransaction();
        assertNotNull(transaction);

        int threadCount = 5;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        List<Exception> exceptions = new ArrayList<>();

        // 启动多个线程并发更新
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

        // 触发并发更新
        startLatch.countDown();
        endLatch.await(30, TimeUnit.SECONDS);

        // 验证结果
        assertTrue(exceptions.isEmpty(), "并发更新过程中发生异常");
        Transaction updatedTransaction = queryTransaction(transaction.getId());
        assertNotNull(updatedTransaction);
        assertNotEquals(TransactionStatus.PENDING, updatedTransaction.getStatus());
    }

    /**
     * 测试并发删除交易
     * 多个线程同时尝试删除同一笔交易，验证并发控制的有效性
     */
    @Test
    void testConcurrentDelete() throws Exception {
        // 创建测试交易
        Transaction transaction = createTestTransaction();
        assertNotNull(transaction);

        int threadCount = 5;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        List<Exception> exceptions = new ArrayList<>();

        // 启动多个线程并发删除
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

        // 触发并发删除
        startLatch.countDown();
        endLatch.await(30, TimeUnit.SECONDS);

        // 验证结果
        assertEquals(threadCount - 1, exceptions.size(), "应该只有一个线程删除成功");
        Transaction deletedTransaction = queryTransaction(transaction.getId());
        assertNull(deletedTransaction);
    }

    /**
     * 创建测试交易
     *
     * @return 创建的交易记录
     * @throws Exception 如果创建过程中发生错误
     */
    private Transaction createTestTransaction() throws Exception {
        TransactionReq req = new TransactionReq();
        req.setTransId("TEST_" + System.currentTimeMillis());
        req.setUserId("TEST_USER");
        req.setAmount(new BigDecimal("100.00"));
        req.setType(TransactionType.DEPOSIT);
        req.setDescription("测试交易");

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
     * 更新交易状态
     *
     * @param id 交易ID
     * @param status 新的交易状态
     * @throws Exception 如果更新过程中发生错误
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
            throw new Exception("更新交易状态失败: " + response.body());
        }
    }

    /**
     * 删除交易
     *
     * @param id 要删除的交易ID
     * @throws Exception 如果删除过程中发生错误
     */
    private void deleteTransaction(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/transactions/" + id))
            .DELETE()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("删除交易失败: " + response.body());
        }
    }

    /**
     * 查询交易
     *
     * @param id 要查询的交易ID
     * @return 交易记录，如果不存在则返回null
     * @throws Exception 如果查询过程中发生错误
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
     * 清理测试数据
     *
     * @throws Exception 如果清理过程中发生错误
     */
    private void clearTestData() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/inner/transactions/clear"))
            .DELETE()
            .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
