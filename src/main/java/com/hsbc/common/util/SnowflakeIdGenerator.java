package com.hsbc.common.util;

import com.hsbc.common.errorhandler.exception.FrameworkException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Snowflake ID Generator
 * Structure:
 * - 1 bit sign (fixed as 0)
 * - 41 bits timestamp (milliseconds)
 * - 10 bits machine ID (5 bits datacenter + 5 bits machine)
 * - 12 bits sequence number
 *
 * @author rd
 * @version 1.0
 * 雪花算法ID生成器
 * 结构：
 * - 1位符号位（固定为0）
 * - 41位时间戳（毫秒级）
 * - 10位工作机器ID（5位数据中心+5位机器ID）
 * - 12位序列号
 */
@Component
public class SnowflakeIdGenerator {
    private final long startEpoch = 1680278400000L; // 2023-04-01 00:00:00.000

    private final long dataCenterIdBits = 5L;
    private final long workerIdBits = 5L;
    private final long sequenceBits = 12L;

    private final long maxDataCenterId = ~(-1L << dataCenterIdBits);
    private final long maxWorkerId = ~(-1L << workerIdBits);
    private final long maxSequence = ~(-1L << sequenceBits);

    private final long workerIdShift = sequenceBits;
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
    private final long timestampShift = sequenceBits + workerIdBits + dataCenterIdBits;

    private long dataCenterId;
    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(
            @Value("${snowflake.datacenter-id:1}") long dataCenterId,
            @Value("${snowflake.worker-id:1}") long workerId) {
        
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("Data center ID must be between 0 and %d", maxDataCenterId));
        }
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("Worker ID must be between 0 and %d", maxWorkerId));
        }
        
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，抛出异常
        if (timestamp < lastTimestamp) {
            throw new FrameworkException(String.format(
                    "Clock moved backwards. Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行序列号自增
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & maxSequence;
            // 序列号用完了，等待下一毫秒
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，序列号重置为0
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 组装ID
        return ((timestamp - startEpoch) << timestampShift)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
} 