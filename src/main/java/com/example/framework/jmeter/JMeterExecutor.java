package com.example.framework.jmeter;

import com.example.framework.jmeter.config.TestConfig;
import com.example.framework.jmeter.generator.JMeterTestGenerator;
import com.example.framework.jmeter.report.TestReportGenerator;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

/**
 * JMeter 执行器，负责执行性能测试并生成报告
 */
public class JMeterExecutor {
    private StandardJMeterEngine jmeterEngine;
    private JMeterTestGenerator testGenerator;
    private TestReportGenerator reportGenerator;

    public JMeterExecutor() {
        // 初始化JMeter引擎
        String jmeterHome = System.getProperty("user.dir");
        JMeterUtils.setJMeterHome(jmeterHome);
        JMeterUtils.loadJMeterProperties(jmeterHome + "/target/classes/jmeter.properties");
        JMeterUtils.initLocale();
        jmeterEngine = new StandardJMeterEngine();
        
        // 初始化测试生成器和报告生成器
        testGenerator = new JMeterTestGenerator();
        reportGenerator = new TestReportGenerator();
    }

    /**
     * 执行性能测试
     * @param config 测试配置
     */
    public void runTest(TestConfig config) {
        try {
            // 开始测试，记录测试信息
            reportGenerator.startTest(config);
            
            // 生成测试计划并执行
            HashTree testPlanTree = testGenerator.generateTestPlan(config);
            jmeterEngine.configure(testPlanTree);
            
            // 记录测试开始时间
            long startTime = System.currentTimeMillis();
            
            // 执行测试
            jmeterEngine.run();
            
            // 计算响应时间
            long responseTime = System.currentTimeMillis() - startTime;
            
            // 判断测试是否成功
            boolean success = responseTime <= config.getExpectedResponseTime();
            String message = String.format("预期响应时间: %dms", config.getExpectedResponseTime());
            
            // 记录测试结果
            reportGenerator.logTestResult(success, message, responseTime);
            
        } catch (Exception e) {
            reportGenerator.logTestResult(false, "测试执行异常: " + e.getMessage(), 0);
            throw new RuntimeException("执行性能测试失败", e);
        } finally {
            // 生成测试报告
            reportGenerator.generateReport();
        }
    }
}
