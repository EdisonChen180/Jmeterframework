package com.example.framework.jmeter;

import com.example.framework.jmeter.config.TestConfig;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class OkxApiTest {

    @Test
    public void testOkxApi() {
        // 创建请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("OK-ACCESS-PROJECT", "test");

        // 构建测试配置
        TestConfig config = TestConfig.builder()
                .testName("OKX API Performance Test")
                .url("https://www.okx.com/api/v5/wallet/post-transaction/transaction-detail-by-txhash")
                .method("GET")
                .threadCount(2)  // 并发用户数
                .loopCount(3)   // 循环次数
                .expectedResponseTime(2000)  // 预期响应时间2秒
                .duration(10)    // 测试持续时间10s
                .chainIndex("195")  // 设置chainIndex参数
                .headers(headers)   // 设置请求头
                .build();

        // 执行测试
        JMeterExecutor executor = new JMeterExecutor();
        executor.runTest(config);
    }
}