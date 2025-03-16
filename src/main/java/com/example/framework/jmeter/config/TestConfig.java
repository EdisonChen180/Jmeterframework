package com.example.framework.jmeter.config;

import lombok.Builder;
import lombok.Data;

/**
 * 测试用例配置类，用于管理测试参数
 */
@Data
@Builder
public class TestConfig {
    /**
     * 测试名称
     */
    private String testName;

    /**
     * 请求URL
     */
    private String url;

    /**
     * 请求方法（GET, POST等）
     */
    private String method;

    /**
     * 请求参数（JSON格式）
     */
    private String requestBody;

    /**
     * 并发用户数
     */
    private int threadCount;

    /**
     * 循环次数
     */
    private int loopCount;

    /**
     * 预期响应时间（毫秒）
     */
    private long expectedResponseTime;

    /**
     * 测试持续时间（秒）
     */
    private int duration;

    /**
     * 是否启用调试模式
     */
    private boolean debugMode;
}