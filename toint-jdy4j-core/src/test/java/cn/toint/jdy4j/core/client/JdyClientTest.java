package cn.toint.jdy4j.core.client;

import cn.toint.jdy4j.core.client.impl.JdyClientImpl;
import cn.toint.jdy4j.core.model.JdyAppRequest;
import cn.toint.jdy4j.core.model.JdyAppResponse;
import cn.toint.jdy4j.core.model.JdyEntryRequest;
import cn.toint.jdy4j.core.model.JdyEntryResponse;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.io.file.FileUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

/**
 * @author Toint
 * @date 2025/6/2
 */
@Slf4j
class JdyClientTest {
    /**
     * 简道云客户端
     */
    private final JdyClient jdyClient = this.init();

    /**
     * 初始化简道云客户端
     */
    private JdyClient init() {
        final File file = FileUtil.file("/Users/toint/repository/data/jdy4j/api-key.txt");
        final String key = FileUtil.readUtf8String(file);

        final JdyClientConfig jdyClientConfig = new JdyClientConfig(key);
        jdyClientConfig.setJdyRequestConsumer(request -> log.info("请求回调: {}", request.getSource()));

        return new JdyClientImpl(jdyClientConfig);
    }

    /**
     * 获取用户应用
     */
    @Test
    void listApp() {
        final JdyAppRequest jdyAppRequest = new JdyAppRequest();
        final List<JdyAppResponse> jdyAppResponses = this.jdyClient.listApp(jdyAppRequest);
        log.info("获取用户应用数量: {}", jdyAppResponses.size());
    }

    /**
     * 获取用户表单
     */
    @Test
    void listEntry() {
        final JdyEntryRequest jdyEntryRequest = new JdyEntryRequest("68383bfeba0b36d412d89aae");
        final List<JdyEntryResponse> jdyEntryResponses = this.jdyClient.listEntry(jdyEntryRequest);
        log.info("获取用户表单数量: {}", jdyEntryResponses.size());
    }

//    /**
//     * 获取用户表单
//     */
//    @Test
//    void listEntry() {
//        final JdyEntryRequest jdyEntryRequest = new JdyEntryRequest("68383bfeba0b36d412d89aae");
//        final List<JdyEntryResponse> jdyEntryResponses = this.jdyClient.listEntry(jdyEntryRequest);
//        log.info("获取用户表单数量: {}", jdyEntryResponses.size());
//    }
}