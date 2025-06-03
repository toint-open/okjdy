package cn.toint.jdy4j.core.client;

import cn.toint.jdy4j.core.annotation.JdyTable;
import cn.toint.jdy4j.core.client.impl.JdyClientImpl;
import cn.toint.jdy4j.core.event.JdyRequestEvent;
import cn.toint.jdy4j.core.model.*;
import cn.toint.tool.util.JacksonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.io.file.FileUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Toint
 * @date 2025/6/2
 */
@Slf4j
class JdyClientTest implements Consumer<JdyRequestEvent> {
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
        jdyClientConfig.setJdyRequestConsumer(this);

        return new JdyClientImpl(jdyClientConfig);
    }

    /**
     * 获取用户应用
     */
    @Test
    void listApp() {
        final JdyAppListRequest jdyAppListRequest = new JdyAppListRequest();
        final List<JdyApp> jdyApps = this.jdyClient.listApp(jdyAppListRequest);
        log.info("获取用户应用数量: {}", jdyApps.size());
        log.info("获取用户应用详情: {}", JacksonUtil.writeValueAsString(jdyApps));
    }

    /**
     * 获取用户表单
     */
    @Test
    void listEntry() {
        final JdyEntryListRequest jdyEntryListRequest = new JdyEntryListRequest("68383bfeba0b36d412d89aae");
        final List<JdyEntry> jdyEntries = this.jdyClient.listEntry(jdyEntryListRequest);
        log.info("获取用户表单数量: {}", jdyEntries.size());
        log.info("获取用户表单详情: {}", JacksonUtil.writeValueAsString(jdyEntries));
    }

    /**
     * 获取表单字段
     */
    @Test
    void listField() {
        final TestJdyDo testJdyDo = new TestJdyDo();
        final JdyFieldListRequest jdyFieldListRequest = new JdyFieldListRequest();
        jdyFieldListRequest.setAppId(testJdyDo.getAppId());
        jdyFieldListRequest.setEntryId(testJdyDo.getEntryId());
        final JdyFieldListResponse jdyFieldListResponse = this.jdyClient.listField(jdyFieldListRequest);
        log.info("获取表单字段数量: {}", jdyFieldListResponse.getWidgets().size());
        log.info("获取表单字段详情: {}", JacksonUtil.writeValueAsString(jdyFieldListResponse));

    }

    /**
     * 获取数据
     */
    @Test
    void listData() {
        final JdyListDataRequest jdyListDataRequest = JdyListDataRequest.of()
                .select()
                .from(TestJdyDo.class)
                .eq(TestJdyDo::getNum, "1");
        final List<TestJdyDo> response = this.jdyClient.listData(jdyListDataRequest, TestJdyDo.class);
        log.info("listData response: {}", JacksonUtil.writeValueAsString(response));
    }

    @Override
    public void accept(final JdyRequestEvent jdyRequestEvent) {
        final JdyRequestEvent.RequestInfo requestInfo = jdyRequestEvent.getSource();
        log.info("请求回调, url: {}", requestInfo.getUrl());
        log.info("请求回调, reqBody: {}", requestInfo.getRequestBody());
        log.info("请求回调, resBody: {}", requestInfo.getResponseBody());
        log.info("请求回调, time: {}", requestInfo.getDurationTime());
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JdyTable(appId = "68383bfeba0b36d412d89aae", entryId = "683cefb4b20f798ec356c27b")
    private static class TestJdyDo extends JdyDo {
        private String str;
        private Number num;
        private Instant time;
        private JdyFile file;
        private JdySub<Sub> sub;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    private static class Sub extends JdySubDo {
        @JsonProperty("sub_str")
        private String subStr;
        @JsonProperty("sub_num")
        private Number subNum;
        @JsonProperty("sub_time")
        private Instant subTime;
        @JsonProperty("sub_file")
        private JdyFile subFile;
    }
}