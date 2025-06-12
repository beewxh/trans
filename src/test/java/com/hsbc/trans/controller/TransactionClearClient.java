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
 * Transaction Data Cleanup Client
 * Used to clean up test data by calling internal interface to delete all transaction records
 *
 * @author rd
 * @version 1.0
 * @since 2025/6/12
 */
@Slf4j
public class TransactionClearClient {

    /**
     * Main method
     * Call internal interface to clean up all transaction data
     *
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        try {
            log.info("Starting to clean up test data...");
            String url = "http://localhost:8080/inner/transactions/clear";

            final HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30)) // Increase timeout because dao layer has 5 seconds sleep
                .build();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals(ResponseCode.SUCC.getCode(), JsonUtils.parseNode(response.body()).get("code").asText());
            log.info("Test data cleanup completed");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
