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
import java.math.BigDecimal;
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
                .eq(TestJdyDo::getF1, BigDecimal.valueOf(1));
        final List<JdyDo> response = this.jdyClient.listData(jdyListDataRequest, JdyDo.class);
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
        @JsonProperty("f1")
        private String f1;

//        @JsonProperty("f2")
//        private String f2;
//
//        @JsonProperty("f3")
//        private Integer f3;
//
//        @JsonProperty("f4")
//        private String f4;
//
//        @JsonProperty("f5")
//        private String f5;
//
//        @JsonProperty("f6")
//        private String f6;
//
//        @JsonProperty("f7")
//        private String f7;
//
//        @JsonProperty("f8")
//        private String f8;
//
//        @JsonProperty("f9")
//        private String f9;
//
//        @JsonProperty("f10")
//        private String f10;
//
//        @JsonProperty("f11")
//        private String f11;
//
//        @JsonProperty("f12")
//        private String f12;
//
//        @JsonProperty("f13")
//        private String f13;
//
//        @JsonProperty("f14")
//        private String f14;
//
//        @JsonProperty("f15")
//        private String f15;
//
//        @JsonProperty("f16")
//        private String f16;
//
//        @JsonProperty("f17")
//        private Subform f17;
//
//        @JsonProperty("f18")
//        private String f18;
//
//        @JsonProperty("f19")
//        private String f19;
//
//        @JsonProperty("f20")
//        private String f20;
//
//        @JsonProperty("f21")
//        private String f21;
//
//        @JsonProperty("f22")
//        private String f22;
//
//        // Subform class
//        @EqualsAndHashCode(callSuper = true)
//        @Data
//        public static class Subform extends BaseJdySubTable {
//
//            @JsonProperty("ff1")
//            private String ff1;
//
//            @JsonProperty("ff2")
//            private String ff2;
//
//            @JsonProperty("ff3")
//            private Integer ff3;
//
//            @JsonProperty("ff4")
//            private String ff4;
//
//            @JsonProperty("ff5")
//            private String ff5;
//
//            @JsonProperty("ff6")
//            private String ff6;
//
//            @JsonProperty("ff7")
//            private String ff7;
//
//            @JsonProperty("ff8")
//            private String ff8;
//
//            @JsonProperty("ff9")
//            private String ff9;
//
//            @JsonProperty("ff10")
//            private String ff10;
//
//            @JsonProperty("ff11")
//            private String ff11;
//
//            @JsonProperty("ff12")
//            private String ff12;
//
//            @JsonProperty("ff13")
//            private String ff13;
//
//            @JsonProperty("ff14")
//            private String ff14;
//
//            @JsonProperty("ff15")
//            private String ff15;
//
//            @JsonProperty("ff16")
//            private String ff16;
//
//            @JsonProperty("ff17")
//            private String ff17;
//
//            @JsonProperty("ff18")
//            private String ff18;
//        }
    }
}