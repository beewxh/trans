package com.hsbc.trans.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.hsbc.common.response.enums.ResponseCode;
import com.hsbc.common.util.JsonUtils;
import com.hsbc.trans.bean.Transaction;
import com.hsbc.trans.enums.ErrorCode;
import static com.hsbc.trans.enums.ErrorCode.PARAM_ERROR;
import static com.hsbc.trans.enums.ErrorCode.TRANSACTION_NOT_FOUND;
import com.hsbc.trans.enums.TransactionType;
import com.hsbc.trans.vo.PageResult;
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
import java.util.List;
import java.util.Random;

/**
 * 交易系统集成测试客户端
 * 通过HTTP接口测试交易系统的各项功能，包括创建、查询、更新和删除交易等操作
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Slf4j
public class TransactionTestClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    private static final String BASE_URL = "http://localhost:8080/api/transactions";
    private Transaction testTransaction;
    private final Random random = new Random();

    /**
     * 测试前置准备
     * 创建一个测试用的交易记录，供后续测试使用
     *
     * @throws Exception 如果创建测试交易失败
     */
    @BeforeEach
    void setUp() throws Exception {
        log.info("Starting test setup...");
        // 创建一个测试交易
        TransactionReq req = new TransactionReq();
        req.setTransId("TEST001");
        req.setUserId("USER001");
        req.setAmount(new BigDecimal("100.00"));
        req.setDescription("测试交易");
        req.setType(TransactionType.DEPOSIT);

        String requestBody = JsonUtils.toJson(req);
        log.info("Creating test transaction request: {}", requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/create"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Create transaction response: {}", response.body());
        
        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());
        testTransaction = JsonUtils.fromJson(JsonUtils.parseNode(response.body()).get("data").toString(), Transaction.class);
        log.info("Test setup completed, created test transaction ID: {}", testTransaction.getId());
    }

    /**
     * 测试后清理
     * 删除测试过程中创建的交易记录
     *
     * @throws Exception 如果清理过程中发生错误
     */
    @AfterEach
    void tearDown() throws Exception {
        if (testTransaction != null && testTransaction.getId() != null) {
            log.info("Starting to clean up test data, deleting transaction ID: {}", testTransaction.getId());
            String url = BASE_URL + "/" + testTransaction.getId() + "/delete";
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            log.info("Test data cleanup completed");
        }
    }

    /**
     * 测试创建交易成功场景
     * 验证系统能够正确创建新的交易记录
     *
     * @throws Exception 如果测试过程中发生错误
     */
    @Test
    void createTransaction_Success() throws Exception {
        log.info("Starting test case: create transaction success...");
        TransactionReq req = new TransactionReq();
        req.setTransId("TEST002" + random.nextInt(1000));
        req.setUserId("USER001");
        req.setAmount(new BigDecimal("200.00"));
        req.setDescription("测试交易2");
        req.setType(TransactionType.WITHDRAWAL);

        String requestBody = JsonUtils.toJson(req);
        log.info("Create transaction request: {}", requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/create"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Create transaction response: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());

        JsonNode data = JsonUtils.parseNode(response.body()).get("data");
        Transaction transaction = JsonUtils.toBean(data.toString(), Transaction.class);
        assertNotNull(transaction);
        assertEquals(req.getTransId(), transaction.getTransId());
        log.info("Create transaction success test passed, created transaction ID: {}", transaction.getId());
    }

    /**
     * 测试创建交易参数验证失败场景
     * 验证系统能够正确处理无效的交易ID格式
     *
     * @throws Exception 如果测试过程中发生错误
     */
    @Test
    void createTransaction_ValidationFailed() throws Exception {
        log.info("Starting test case: create transaction parameter validation failure...");
        TransactionReq req = new TransactionReq();
        // 缺少必要参数
        req.setDescription("测试交易");

        String requestBody = JsonUtils.toJson(req);
        log.info("Create transaction request (missing required parameters): {}", requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/create"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Create transaction response: {}", response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(PARAM_ERROR.getCode()));
        log.info("Create transaction parameter validation failure test passed");
    }

    /**
     * 测试创建重复交易场景
     * 验证系统能够正确处理重复的交易ID
     *
     * @throws Exception 如果测试过程中发生错误
     */
    @Test
    void createTransaction_DuplicateTransId() throws Exception {
        log.info("Starting test case: create transaction duplicate transId failure...");
        TransactionReq req = new TransactionReq();
        // 使用与setUp中相同的transId
        req.setTransId("TEST001");
        req.setUserId("USER002");
        req.setAmount(new BigDecimal("300.00"));
        req.setDescription("测试重复transId");
        req.setType(TransactionType.DEPOSIT);

        String requestBody = JsonUtils.toJson(req);
        log.info("Create transaction request (duplicate transId): {}", requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/create"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Create transaction response: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ErrorCode.TRANSACTION_DUPLICATE.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());
        log.info("Create transaction duplicate transId test passed");
    }

    /**
     * 测试获取单个交易成功场景
     * 验证系统能够正确返回指定ID的交易记录
     *
     * @throws Exception 如果测试过程中发生错误
     */
    @Test
    void getTransaction_Success() throws Exception {
        log.info("Starting test case: get single transaction success...");
        String url = BASE_URL + "/" + testTransaction.getId();
        log.info("Request URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Get transaction response: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());

        JsonNode data = JsonUtils.parseNode(response.body()).get("data");
        Transaction transaction = JsonUtils.toBean(data.toString(), Transaction.class);
        assertEquals(testTransaction.getId(), transaction.getId());
        log.info("Get single transaction success test passed");
    }

    /**
     * 测试获取交易参数验证失败场景
     * 验证系统能够正确处理无效的交易ID格式
     *
     * @throws Exception 如果测试过程中发生错误
     */
    @Test
    void getTransaction_ValidationFailed() throws Exception {
        log.info("Starting test case: get single transaction invalid ID format...");
        String url = BASE_URL + "/invalid-id";
        log.info("Request URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Get transaction response: {}", response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(PARAM_ERROR.getCode()));
        log.info("Get single transaction invalid ID format test passed");
    }

    /**
     * 测试分页获取交易列表成功场景
     * 验证系统能够正确返回分页的交易记录
     *
     * @throws Exception 如果测试过程中发生错误
     */
    @Test
    void getTransactionPage_Success() throws Exception {
        log.info("Starting test case: get transaction list with pagination success...");
        String url = BASE_URL + "/page?page=0&size=10";
        log.info("Request URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Get transaction list response: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());

        String dataJson = JsonUtils.parseNode(response.body()).get("data").toString();
        PageResult<Transaction> pageResult = JsonUtils.fromJson(dataJson, new TypeReference<PageResult<Transaction>>() {});
        assertNotNull(pageResult);
        assertTrue(pageResult.getTotalElements() > 0);
        log.info("Get transaction list with pagination success test passed, total records: {}", pageResult.getTotalElements());
    }

    /**
     * 测试分页参数验证失败场景
     * 验证系统能够正确处理无效的分页参数
     *
     * @throws Exception 如果测试过程中发生错误
     */
    @Test
    void getTransactionPage_ValidationFailed() throws Exception {
        log.info("Starting test case: get transaction list with pagination parameter validation failure...");
        String url = BASE_URL + "/page?page=-1&size=0";
        log.info("Request URL (invalid parameters): {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Get transaction list response: {}", response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(PARAM_ERROR.getCode()));
        log.info("Get transaction list with pagination parameter validation failure test passed");
    }

    /**
     * 测试分页超出范围场景
     * 验证系统能够正确处理超出数据范围的页码
     *
     * @throws Exception 如果测试过程中发生错误
     */
    @Test
    void getTransactionPage_PageOutOfRange() throws Exception {
        log.info("Starting test case: get transaction list with pagination page out of range...");
        String url = BASE_URL + "/page?page=999999&size=10";
        log.info("Request URL (page out of range): {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Get transaction list response: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());
        
        String dataJson = JsonUtils.parseNode(response.body()).get("data").toString();
        PageResult<Transaction> pageResult = JsonUtils.fromJson(dataJson, new TypeReference<PageResult<Transaction>>() {});
        assertNotNull(pageResult);
        assertTrue(pageResult.getContent().isEmpty());
        assertEquals(0, pageResult.getContent().size());
        log.info("Get transaction list with pagination page out of range test passed");
    }

    /**
     * 测试获取所有交易成功场景
     * 验证系统能够正确返回所有交易记录
     *
     * @throws Exception 如果测试过程中发生错误
     */
    @Test
    void getAllTransactions_Success() throws Exception {
        log.info("Starting test case: get all transactions success...");
        String url = BASE_URL + "/all";
        log.info("Request URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Get all transactions response: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());

        String dataJson = JsonUtils.parseNode(response.body()).get("data").toString();
        List<Transaction> transactions = JsonUtils.fromJson(dataJson, new TypeReference<List<Transaction>>() {});
        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());
        log.info("Get all transactions success test passed, transaction count: {}", transactions.size());
    }

    /**
     * 测试删除交易成功场景
     * 验证系统能够正确删除指定的交易记录
     *
     * @throws Exception 如果测试过程中发生错误
     */
    @Test
    void deleteTransaction_Success() throws Exception {
        log.info("Starting test case: delete transaction success...");
        String url = BASE_URL + "/" + testTransaction.getId() + "/delete";
        log.info("Request URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Delete transaction response: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(200, response.statusCode());
        log.info("Delete transaction success test passed");
    }

    /**
     * 测试删除不存在交易场景
     * 验证系统能够正确处理删除不存在的交易ID
     *
     * @throws Exception 如果测试过程中发生错误
     */
    @Test
    void deleteTransaction_ValidationFailed() throws Exception {
        log.info("Starting test case: delete transaction ID not found...");
        String url = BASE_URL + "/-1/delete";
        log.info("Request URL (ID not found): {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Delete transaction response: {}", response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(TRANSACTION_NOT_FOUND.getCode()));
        log.info("Delete transaction ID not found test passed");
    }

    // 根据transId获取交易 - 成功用例
    @Test
    void getTransactionByTransId_Success() throws Exception {
        log.info("Starting test case: get transaction by transId success...");
        String url = BASE_URL + "/trans/" + testTransaction.getTransId();
        log.info("Request URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Get transaction response: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());

        JsonNode data = JsonUtils.parseNode(response.body()).get("data");
        Transaction transaction = JsonUtils.toBean(data.toString(), Transaction.class);
        assertEquals(testTransaction.getTransId(), transaction.getTransId());
        log.info("Get transaction by transId success test passed");
    }

    // 根据transId获取交易 - 失败用例（transId不存在）
    @Test
    void getTransactionByTransId_ValidationFailed() throws Exception {
        log.info("Starting test case: get transaction by transId not found...");
        String url = BASE_URL + "/trans/NON_EXISTENT_ID";
        log.info("Request URL (transId not found): {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Get transaction response: {}", response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(TRANSACTION_NOT_FOUND.getCode()));
        log.info("Get transaction by transId not found test passed");
    }
}
