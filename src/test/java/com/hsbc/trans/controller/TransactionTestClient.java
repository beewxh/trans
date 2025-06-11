package com.hsbc.trans.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.hsbc.common.response.enums.ResponseCode;
import com.hsbc.common.util.JsonUtils;
import com.hsbc.trans.bean.Transaction;
import com.hsbc.trans.enums.ErrorCode;
import static com.hsbc.trans.enums.ErrorCode.PARAM_ERROR;
import static com.hsbc.trans.enums.ErrorCode.TRANSACTION_NOT_FOUND;
import com.hsbc.trans.enums.TransactionStatus;
import com.hsbc.trans.enums.TransactionType;
import com.hsbc.trans.vo.PageResult;
import com.hsbc.trans.vo.TransactionReq;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
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
 * @ClassName TransactionTestClient
 * @Description TODO
 * @Author rd
 * @Date 2025/6/11 22:00
 * @Version 1.0
 **/

@Slf4j
public class TransactionTestClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    private static final String BASE_URL = "http://localhost:8080/api/transactions";
    private Transaction testTransaction;

    private final Random random = new Random();

    @BeforeEach
    void setUp() throws Exception {
        log.info("开始执行测试前置准备...");
        // 创建一个测试交易
        TransactionReq req = new TransactionReq();
        req.setTransId("TEST001");
        req.setUserId("USER001");
        req.setAmount(new BigDecimal("100.00"));
        req.setDescription("测试交易");
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
        testTransaction = JsonUtils.fromJson(JsonUtils.parseNode(response.body()).get("data").toString(), Transaction.class);
        log.info("测试前置准备完成，创建的测试交易ID: {}", testTransaction.getId());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (testTransaction != null && testTransaction.getId() != null) {
            log.info("开始清理测试数据，删除交易ID: {}", testTransaction.getId());
            String url = BASE_URL + "/" + testTransaction.getId() + "/delete";
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            log.info("测试数据清理完成");
        }
    }

    // 创建交易 - 成功用例
    @Test
    void createTransaction_Success() throws Exception {
        log.info("开始测试创建交易成功用例...");
        TransactionReq req = new TransactionReq();
        req.setTransId("TEST002" + random.nextInt(1000));
        req.setUserId("USER001");
        req.setAmount(new BigDecimal("200.00"));
        req.setDescription("测试交易2");
        req.setType(TransactionType.WITHDRAWAL);

        String requestBody = JsonUtils.toJson(req);
        log.info("创建交易请求: {}", requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/create"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("创建交易响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());

        JsonNode data = JsonUtils.parseNode(response.body()).get("data");
        Transaction transaction = JsonUtils.toBean(data.toString(), Transaction.class);
        assertNotNull(transaction);
        assertEquals(req.getTransId(), transaction.getTransId());
        log.info("创建交易成功测试通过，创建的交易ID: {}", transaction.getId());
    }

    // 创建交易 - 失败用例（参数验证错误）
    @Test
    void createTransaction_ValidationFailed() throws Exception {
        log.info("开始测试创建交易参数验证失败用例...");
        TransactionReq req = new TransactionReq();
        // 缺少必要参数
        req.setDescription("测试交易");

        String requestBody = JsonUtils.toJson(req);
        log.info("创建交易请求（缺少必要参数）: {}", requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/create"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("创建交易响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(PARAM_ERROR.getCode()));
        log.info("创建交易参数验证失败测试通过");
    }

    // 创建交易 - 失败用例（重复的transId）
    @Test
    void createTransaction_DuplicateTransId() throws Exception {
        log.info("开始测试创建交易重复transId失败用例...");
        TransactionReq req = new TransactionReq();
        // 使用与setUp中相同的transId
        req.setTransId("TEST001");
        req.setUserId("USER002");
        req.setAmount(new BigDecimal("300.00"));
        req.setDescription("测试重复transId");
        req.setType(TransactionType.DEPOSIT);

        String requestBody = JsonUtils.toJson(req);
        log.info("创建交易请求（重复transId）: {}", requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/create"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("创建交易响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ErrorCode.TRANSACTION_DUPLICATE.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());
        log.info("创建交易重复transId测试通过");
    }

    // 获取单个交易 - 成功用例
    @Test
    void getTransaction_Success() throws Exception {
        log.info("开始测试获取单个交易成功用例...");
        String url = BASE_URL + "/" + testTransaction.getId();
        log.info("请求URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("获取交易响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());

        JsonNode data = JsonUtils.parseNode(response.body()).get("data");
        Transaction transaction = JsonUtils.toBean(data.toString(), Transaction.class);
        assertEquals(testTransaction.getId(), transaction.getId());
        log.info("获取单个交易成功测试通过");
    }

    // 获取单个交易 - 失败用例（ID格式错误）
    @Test
    void getTransaction_ValidationFailed() throws Exception {
        log.info("开始测试获取单个交易ID格式错误用例...");
        String url = BASE_URL + "/invalid-id";
        log.info("请求URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("获取交易响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(PARAM_ERROR.getCode()));
        log.info("获取单个交易ID格式错误测试通过");
    }

    // 分页获取交易列表 - 成功用例
    @Test
    void getTransactionPage_Success() throws Exception {
        log.info("开始测试分页获取交易列表成功用例...");
        String url = BASE_URL + "/page?page=0&size=10";
        log.info("请求URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("获取交易列表响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());

        String dataJson = JsonUtils.parseNode(response.body()).get("data").toString();
        PageResult<Transaction> pageResult = JsonUtils.fromJson(dataJson, new TypeReference<PageResult<Transaction>>() {});
        assertNotNull(pageResult);
        assertTrue(pageResult.getTotalElements() > 0);
        log.info("分页获取交易列表成功测试通过，总记录数: {}", pageResult.getTotalElements());
    }

    // 分页获取交易列表 - 失败用例（参数验证错误）
    @Test
    void getTransactionPage_ValidationFailed() throws Exception {
        log.info("开始测试分页获取交易列表参数验证失败用例...");
        String url = BASE_URL + "/page?page=-1&size=0";
        log.info("请求URL（参数错误）: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("获取交易列表响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(PARAM_ERROR.getCode()));
        log.info("分页获取交易列表参数验证失败测试通过");
    }

    // 分页获取交易列表 - 失败用例（页数超出范围）
    @Test
    void getTransactionPage_PageOutOfRange() throws Exception {
        log.info("开始测试分页获取交易列表页数超出范围用例...");
        String url = BASE_URL + "/page?page=999999&size=10";
        log.info("请求URL（页数超出范围）: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("获取交易列表响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());
        
        String dataJson = JsonUtils.parseNode(response.body()).get("data").toString();
        PageResult<Transaction> pageResult = JsonUtils.fromJson(dataJson, new TypeReference<PageResult<Transaction>>() {});
        assertNotNull(pageResult);
        assertTrue(pageResult.getContent().isEmpty());
        assertEquals(0, pageResult.getContent().size());
        log.info("分页获取交易列表页数超出范围测试通过");
    }

    // 获取所有交易 - 成功用例
    @Test
    void getAllTransactions_Success() throws Exception {
        log.info("开始测试获取所有交易成功用例...");
        String url = BASE_URL + "/all";
        log.info("请求URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("获取所有交易响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());

        String dataJson = JsonUtils.parseNode(response.body()).get("data").toString();
        List<Transaction> transactions = JsonUtils.fromJson(dataJson, new TypeReference<List<Transaction>>() {});
        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());
        log.info("获取所有交易成功测试通过，交易数量: {}", transactions.size());
    }

    // 更新交易状态 - 成功用例
    @Test
    void updateTransactionStatus_Success() throws Exception {
        log.info("开始测试更新交易状态成功用例...");
        String url = String.format("%s/%d/update?status=%s&description=%s",
            BASE_URL,
            testTransaction.getId(),
            TransactionStatus.PROCESSING.name(),
            "更新状态测试");
        log.info("请求URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("更新交易状态响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());

        JsonNode data = JsonUtils.parseNode(response.body()).get("data");
        Transaction transaction = JsonUtils.toBean(data.toString(), Transaction.class);
        assertEquals(TransactionStatus.PROCESSING, transaction.getStatus());
        log.info("更新交易状态成功测试通过");
    }

    // 更新交易状态 - 失败用例，状态变迁不合法
    @Test
    void updateTransactionStatus_StatusFailed() throws Exception {
        log.info("开始测试更新交易状态成功用例...");
        String url = String.format("%s/%d/update?status=%s&description=%s",
            BASE_URL,
            testTransaction.getId(),
            TransactionStatus.COMPLETED.name(),
            "更新状态测试");
        log.info("请求URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("更新交易状态响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ErrorCode.TRANSACTION_UPDATE_STATUS_INVALID.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());
        log.info("更新交易状态变迁失败测试通过");
    }

    // 更新交易状态 - 失败用例（状态枚举值错误）
    @Test
    void updateTransactionStatus_ValidationFailed() throws Exception {
        log.info("开始测试更新交易状态枚举值错误用例...");
        String url = String.format("%s/%d/update?status=%s&description=%s",
            BASE_URL,
            testTransaction.getId(),
            "INVALID_STATUS",
            "更新状态测试");
        log.info("请求URL（状态值错误）: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("更新交易状态响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(PARAM_ERROR.getCode()));
        log.info("更新交易状态枚举值错误测试通过");
    }

    // 删除交易 - 成功用例
    @Test
    void deleteTransaction_Success() throws Exception {
        log.info("开始测试删除交易成功用例...");
        String url = BASE_URL + "/" + testTransaction.getId() + "/delete";
        log.info("请求URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("删除交易响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(200, response.statusCode());
        log.info("删除交易成功测试通过");
    }

    // 删除交易 - 失败用例（ID不存在）
    @Test
    void deleteTransaction_ValidationFailed() throws Exception {
        log.info("开始测试删除交易ID不存在用例...");
        String url = BASE_URL + "/-1/delete";
        log.info("请求URL（ID不存在）: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("删除交易响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(TRANSACTION_NOT_FOUND.getCode()));
        log.info("删除交易ID不存在测试通过");
    }

    // 根据transId获取交易 - 成功用例
    @Test
    void getTransactionByTransId_Success() throws Exception {
        log.info("开始测试根据transId获取交易成功用例...");
        String url = BASE_URL + "/trans/" + testTransaction.getTransId();
        log.info("请求URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("获取交易响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());

        JsonNode data = JsonUtils.parseNode(response.body()).get("data");
        Transaction transaction = JsonUtils.toBean(data.toString(), Transaction.class);
        assertEquals(testTransaction.getTransId(), transaction.getTransId());
        log.info("根据transId获取交易成功测试通过");
    }

    // 根据transId获取交易 - 失败用例（transId不存在）
    @Test
    void getTransactionByTransId_ValidationFailed() throws Exception {
        log.info("开始测试根据transId获取交易不存在用例...");
        String url = BASE_URL + "/trans/NON_EXISTENT_ID";
        log.info("请求URL（transId不存在）: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("获取交易响应: {}", response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(TRANSACTION_NOT_FOUND.getCode()));
        log.info("根据transId获取交易不存在测试通过");
    }
}
