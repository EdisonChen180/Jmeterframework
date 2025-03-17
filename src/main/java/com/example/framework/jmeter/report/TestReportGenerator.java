package com.example.framework.jmeter.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.example.framework.jmeter.config.TestConfig;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 测试报告生成器
 */
@Slf4j
public class TestReportGenerator {
    private ExtentReports extentReports;
    private ExtentTest currentTest;
    private String reportPath;
    
    // 性能指标统计
    private long startTime;
    private long totalRequests;
    private long totalResponseTime;
    private long maxResponseTime;
    private long errorCount;
    private int currentThreadCount;
    private final Object lock = new Object();

    public TestReportGenerator() {
        // 初始化ExtentReports
        extentReports = new ExtentReports();
        
        // 设置报告文件路径
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        reportPath = "test-output/performance-test-report_" + timestamp + ".html";
        
        // 配置报告
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setEncoding("UTF-8");
        sparkReporter.config().setDocumentTitle("性能测试报告");
        sparkReporter.config().setReportName("JMeter性能测试结果");
        sparkReporter.config().setTimeStampFormat("yyyy年MM月dd日 HH:mm:ss");
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().enableTimeline(true);
        
        extentReports.attachReporter(sparkReporter);
    }

    /**
     * 开始测试，记录测试信息
     * @param config 测试配置
     */
    public void startTest(TestConfig config) {
        currentTest = extentReports.createTest(config.getTestName());
        
        // 初始化性能指标
        startTime = System.currentTimeMillis();
        totalRequests = 0;
        totalResponseTime = 0;
        maxResponseTime = 0;
        errorCount = 0;
        currentThreadCount = config.getThreadCount();
        
        // 控制台输出测试配置信息
        log.info("========== 开始执行性能测试 ==========");
        log.info("测试名称: {}" , config.getTestName());
        log.info("测试URL: {}" , config.getUrl());
        log.info("请求方法: {}" , config.getMethod());
        log.info("并发用户数: {}" , config.getThreadCount());
        log.info("循环次数: {}" , config.getLoopCount());
        log.info("预期响应时间: {}ms" , config.getExpectedResponseTime());
        
        if (config.getRequestBody() != null && !config.getRequestBody().isEmpty()) {
            log.info("请求体: {}" , config.getRequestBody());
        }
        log.info("=====================================");
        
        // HTML报告记录
        currentTest.info("测试URL: " + config.getUrl());
        currentTest.info("请求方法: " + config.getMethod());
        currentTest.info("并发用户数: " + config.getThreadCount());
        currentTest.info("循环次数: " + config.getLoopCount());
        currentTest.info("预期响应时间: " + config.getExpectedResponseTime() + "ms");
        
        if (config.getRequestBody() != null && !config.getRequestBody().isEmpty()) {
            currentTest.info("请求体: " + config.getRequestBody());
        }
    }

    /**
     * 记录测试结果
     * @param success 是否成功
     * @param message 测试信息
     * @param responseTime 响应时间
     */
    public void logTestResult(boolean success, String message, long responseTime) {
        String resultMessage = String.format("%s，实际响应时间: %dms", message, responseTime);
        
        // 更新性能指标
        synchronized (lock) {
            totalRequests++;
            totalResponseTime += responseTime;
            maxResponseTime = Math.max(maxResponseTime, responseTime);
            if (!success) {
                errorCount++;
            }
            
            // 计算并输出实时性能指标
            long currentTime = System.currentTimeMillis();
            double duration = (currentTime - startTime) / 1000.0;
            double tps = totalRequests / duration;
            double avgResponseTime = totalResponseTime / (double) totalRequests;
            double errorRate = (errorCount / (double) totalRequests) * 100;
            
            // 控制台输出性能指标
            log.info("\n┌──────────────────────┬──────────────────┐");
            log.info("│     性能指标统计   │      值         │");
            log.info("├──────────────────────┼──────────────────┤");
            log.info("│ TPS (每秒事务数)    │ {:>14.2f} │", tps);
            log.info("│ 平均响应时间(ms)    │ {:>14.2f} │", avgResponseTime);
            log.info("│ 最大响应时间(ms)    │ {:>14d} │", maxResponseTime);
            log.info("│ 当前并发用户数      │ {:>14d} │", currentThreadCount);
            log.info("│ 错误率(%)           │ {:>14.2f} │", errorRate);
            log.info("└──────────────────────┴──────────────────┘");
        }
        
        // 控制台输出测试结果
        if (success) {
            log.info("✓ 测试通过 - {}", resultMessage);
        } else {
            log.error("✗ 测试失败 - {}", resultMessage);
        }
        
        // HTML报告记录
        if (currentTest != null) {
            if (success) {
                currentTest.log(Status.PASS, String.format("测试通过 - %s", resultMessage));
            } else {
                currentTest.log(Status.FAIL, String.format("测试失败 - %s", resultMessage));
            }
        }
    }

    /**
     * 生成测试报告
     */
    public void generateReport() {
        if (extentReports != null) {
            extentReports.flush();
            log.info("========== 测试报告生成完成 ==========");
            log.info("报告路径: {}", reportPath);
            log.info("=====================================\n");
        }
    }
}