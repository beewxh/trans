package com.hsbc.common.util;

import com.hsbc.common.utils.JsonUtils;
import com.hsbc.trans.bean.Transaction;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @ClassName JsonUtilsTest
 * @Description TODO
 * @Author rd
 * @Date 2025/6/11 17:38
 * @Version 1.0
 **/

@Slf4j
public class JsonUtilsTest {

    public static void main(String[] args) {

        Transaction transaction = new Transaction();
        String json = JsonUtils.toJson(transaction);

// JSON转对象
        transaction = JsonUtils.toBean(json, Transaction.class);

// JSON转List
        String jsonArray = "[{\"id\":1},{\"id\":2}]";
        List<Transaction> transactions = JsonUtils.toList(jsonArray, Transaction.class);

// JSON转Map
        String jsonMap = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
        Map<String, String> map = JsonUtils.toMap(jsonMap, String.class, String.class);
        log.info("map = {}", map);
        Map<String, Object> map2 = JsonUtils.toMap(jsonMap);
        log.info("map2 = {}", map2);


        Map<String, Object> map3 = Map.of("name", "wangshuli",
            "age", 40,
            "skills", List.of("java", "python"),
            "education", Map.of("master", "ts", "bachelor", "tj"));

        String jsonString = JsonUtils.toJson(map3);
        log.info("jsonString = {}", jsonString);

        map3 = JsonUtils.toMap(jsonString);
        log.info("map3 = {}", map3);

    }
}
