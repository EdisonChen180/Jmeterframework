package com.example.framework.jmeter;

import com.example.framework.jmeter.config.TestConfig;
import org.junit.Test;

/**
 * JMeter性能测试示例
 */
public class JMeterPerformanceTest {

    @Test
    public void testHttpEndpoint() {
        // 创建测试配置
        TestConfig config = TestConfig.builder()
                .testName("API性能测试示例")
                .url("http://localhost:8080/api/test")
                .method("GET")
                .threadCount(10)  // 10个并发用户
                .loopCount(100)   // 每个用户执行100次
                .expectedResponseTime(500)  // 预期响应时间500ms
                .duration(60)     // 测试持续60秒
                .debugMode(true)  // 启用调试模式
                .build();

        // 创建执行器并运行测试
        JMeterExecutor executor = new JMeterExecutor();
        executor.runTest(config);
    }

    @Test
    public void testPostEndpoint() {
        // 创建测试配置
        TestConfig config = TestConfig.builder()
                .testName("POST接口性能测试")
                .url("http://localhost:8080/api/users")
                .method("POST")
                .requestBody("{\"name\":\"test\",\"age\":25}")
                .threadCount(5)   // 5个并发用户
                .loopCount(50)    // 每个用户执行50次
                .expectedResponseTime(1000)  // 预期响应时间1000ms
                .duration(30)     // 测试持续30秒
                .debugMode(false) // 关闭调试模式
                .build();

        // 创建执行器并运行测试
        JMeterExecutor executor = new JMeterExecutor();
        executor.runTest(config);
    }
}