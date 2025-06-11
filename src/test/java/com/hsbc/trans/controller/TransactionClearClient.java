package com.hsbc.trans.controller;

import com.hsbc.common.response.enums.ResponseCode;
import com.hsbc.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * 交易数据清理客户端
 * 用于清理测试数据，通过调用内部接口删除所有交易记录
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Slf4j
public class TransactionClearClient {

    /**
     * 主方法
     * 调用内部接口清理所有交易数据
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        try {
            log.info("开始清理测试数据...");
            String url = "http://localhost:8080/inner/transactions/clear";

            final HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30)) // 增加超时时间，因为dao层有5秒sleep
                .build();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());
            log.info("测试数据清理完成");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
