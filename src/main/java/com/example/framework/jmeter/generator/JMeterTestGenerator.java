package com.example.framework.jmeter.generator;

import com.example.framework.jmeter.config.TestConfig;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jorphan.collections.HashTree;

/**
 * JMeter测试用例生成器，根据配置生成JMeter测试用例
 */
public class JMeterTestGenerator {
    
    /**
     * 生成JMeter测试计划
     * @param config 测试配置
     * @return HashTree 测试计划树
     */
    public HashTree generateTestPlan(TestConfig config) {
        // 创建测试计划
        TestPlan testPlan = new TestPlan(config.getTestName());
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        // 创建HashTree
        HashTree testPlanTree = new HashTree();
        HashTree threadGroupHashTree = testPlanTree.add(testPlan);

        // 创建线程组
        ThreadGroup threadGroup = createThreadGroup(config);
        HashTree httpSamplerTree = threadGroupHashTree.add(threadGroup);

        // 创建HTTP请求
        HTTPSamplerProxy httpSampler = createHttpSampler(config);
        httpSamplerTree.add(httpSampler);

        return testPlanTree;
    }

    /**
     * 创建线程组
     * @param config 测试配置
     * @return ThreadGroup
     */
    private ThreadGroup createThreadGroup(TestConfig config) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName(config.getTestName() + " Thread Group");
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        
        // 设置线程数和循环次数
        threadGroup.setNumThreads(config.getThreadCount());
        
        LoopController loopController = new LoopController();
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.setLoops(config.getLoopCount());
        loopController.setFirst(true);
        loopController.initialize();
        
        threadGroup.setSamplerController(loopController);
        return threadGroup;
    }

    /**
     * 创建HTTP请求采样器
     * @param config 测试配置
     * @return HTTPSamplerProxy
     */
    private HTTPSamplerProxy createHttpSampler(TestConfig config) {
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        httpSampler.setName(config.getTestName() + " HTTP Request");
        
        // 设置请求URL和方法
        httpSampler.setDomain(UrlUtils.extractDomain(config.getUrl()));
        httpSampler.setPath(UrlUtils.extractPath(config.getUrl()));
        httpSampler.setMethod(config.getMethod());
        
        // 如果有请求体，设置请求体
        if (config.getRequestBody() != null && !config.getRequestBody().isEmpty()) {
            httpSampler.setPostBodyRaw(true);
            httpSampler.addNonEncodedArgument("", config.getRequestBody(), "");
        }
        
        return httpSampler;
    }


}