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
 * @ClassName TransactionSyncTestClient
 * @Description 交易并发测试客户端
 * @Author rd
 * @Date 2025/6/12 05:43
 * @Version 1.0
 **/
@Slf4j
public class TransactionSyncTestClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(30)) // 增加超时时间，因为dao层有5秒sleep
        .build();

    private static final String BASE_URL = "http://localhost:8080/api/transactions";
    private Transaction testTransaction;

    @BeforeEach
    void setUp() throws Exception {
        log.info("开始执行测试前置准备...");
        // 创建一个测试交易
        TransactionReq req = new TransactionReq();
        req.setTransId("SYNC_TEST001");
        req.setUserId("USER001");
        req.setAmount(new BigDecimal("100.00"));
        req.setDescription("并发测试交易");
        req.setType(TransactionType.DEPOSIT);

        String requestBody = JsonUtils.toJson(req);
        log.info("创建测试交易请求: {}", requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/create"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("创建测试交易响应: {}", response.body());
        
        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());
        JsonNode data = JsonUtils.parseNode(response.body()).get("data");
        testTransaction = JsonUtils.toBean(data.toString(), Transaction.class);
        log.info("测试前置准备完成，创建的测试交易ID: {}", testTransaction.getId());
    }

    @AfterEach
    void tearDown() throws Exception {
        log.info("开始清理测试数据...");
        String url = "http://localhost:8080/inner/transactions/clear";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());
        log.info("测试数据清理完成");
        testTransaction = null;
    }

    @Test
    void testConcurrentUpdateStatus() throws Exception {
        log.info("开始测试并发更新交易状态...");
        int threadCount = 3;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        List<String> executionOrder = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // 创建多个线程同时更新同一条记录
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    startLatch.await(); // 等待统一开始
                    String threadName = Thread.currentThread().getName();
                    log.info("线程 {} 开始执行更新操作", threadName);

                    String url = String.format("%s/%d/update?status=%s&description=%s",
                        BASE_URL,
                        testTransaction.getId(),
                        TransactionStatus.PROCESSING.name(),
                        "并发更新测试-" + index);

                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                    executionOrder.add("线程-" + index + " 开始");
                    
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    log.info("线程 {} 更新响应: {}", threadName, response.body());
                    
                    executionOrder.add("线程-" + index + " 结束");
                    
                    assertEquals(200, response.statusCode());
                    assertTrue(response.body().contains(ResponseCode.SUCC.getCode()));
                } catch (Exception e) {
                    log.error("线程执行异常", e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        
        boolean allFinished = endLatch.await(60, TimeUnit.SECONDS);
        assertTrue(allFinished, "并发测试超时");
        
        log.info("执行顺序: {}", executionOrder);
        for (int i = 0; i < threadCount; i++) {
            int startIndex = executionOrder.indexOf("线程-" + i + " 开始");
            int endIndex = executionOrder.indexOf("线程-" + i + " 结束");
            assertTrue(startIndex >= 0 && endIndex >= 0 && endIndex > startIndex,
                "线程 " + i + " 的执行顺序不正确");
        }
        
        executorService.shutdown();
        log.info("并发更新测试完成");
    }

    @Test
    void testConcurrentDelete() throws Exception {
        log.info("开始测试并发删除交易...");
        int threadCount = 2;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        List<String> executionOrder = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    String threadName = Thread.currentThread().getName();
                    log.info("线程 {} 开始执行删除操作", threadName);

                    String url = BASE_URL + "/" + testTransaction.getId() + "/delete";

                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                    executionOrder.add("线程-" + index + " 开始");
                    
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    log.info("线程 {} 删除响应: {}", threadName, response.body());
                    
                    executionOrder.add("线程-" + index + " 结束");
                    
                    assertEquals(200, response.statusCode());
                } catch (Exception e) {
                    log.error("线程执行异常", e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        
        boolean allFinished = endLatch.await(60, TimeUnit.SECONDS);
        assertTrue(allFinished, "并发测试超时");
        
        log.info("执行顺序: {}", executionOrder);
        for (int i = 0; i < threadCount; i++) {
            int startIndex = executionOrder.indexOf("线程-" + i + " 开始");
            int endIndex = executionOrder.indexOf("线程-" + i + " 结束");
            assertTrue(startIndex >= 0 && endIndex >= 0 && endIndex > startIndex,
                "线程 " + i + " 的执行顺序不正确");
        }
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/" + testTransaction.getId()))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(response.body().contains(TRANSACTION_NOT_FOUND.getCode()));
        
        executorService.shutdown();
        log.info("并发删除测试完成");
    }
}
