package com.example.framework.jmeter.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.example.framework.jmeter.config.TestConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 测试报告生成器
 */
public class TestReportGenerator {
    private ExtentReports extentReports;
    private ExtentTest currentTest;
    private String reportPath;

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
        sparkReporter.config().setJs("document.charset = 'UTF-8';");
        sparkReporter.config().setCss(".content { font-family: 'Microsoft YaHei', sans-serif; }");
        
        extentReports.attachReporter(sparkReporter);
    }

    /**
     * 开始测试，记录测试信息
     * @param config 测试配置
     */
    public void startTest(TestConfig config) {
        currentTest = extentReports.createTest(config.getTestName());
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
        if (currentTest != null) {
            if (success) {
                currentTest.log(Status.PASS, String.format("测试通过 - %s，实际响应时间: %dms", message, responseTime));
            } else {
                currentTest.log(Status.FAIL, String.format("测试失败 - %s，实际响应时间: %dms", message, responseTime));
            }
        }
    }

    /**
     * 生成测试报告
     */
    public void generateReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}