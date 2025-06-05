package cn.toint.jdy4j.core.client;

import cn.toint.jdy4j.core.annotation.JdyTable;
import cn.toint.jdy4j.core.client.impl.JdyClientImpl;
import cn.toint.jdy4j.core.config.JdyClientConfig;
import cn.toint.jdy4j.core.model.*;
import cn.toint.tool.util.Assert;
import cn.toint.tool.util.JacksonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.data.id.IdUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
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
        // 读取密钥
        final File file = FileUtil.file("/Users/toint/repository/data/jdy4j/api-key.txt");
        final String key = FileUtil.readUtf8String(file);

        // 初始化客户端
        final JdyClientConfig jdyClientConfig = new JdyClientConfig(key);
        jdyClientConfig.setJdyRequestConsumer(jdyRequestEvent -> log.info("请求回调: {}", JacksonUtil.writeValueAsString(jdyRequestEvent.getSource())));
        return new JdyClientImpl(jdyClientConfig);
    }

    /**
     * 测试单例
     */
    @Test
    void testSingle() {
        // 创建测试数据
        final TestJdyDo testDo = this.createTestDo();
        final TestJdyDo saveDataResponse = this.jdyClient.saveData(JdyDataSaveRequest.of(testDo), TestJdyDo.class);

        // 修改测试数据
        saveDataResponse.setStr(IdUtil.fastSimpleUUID());
        saveDataResponse.setTime(null);
        final TestJdyDo updateDataResponse = this.jdyClient.updateData(JdyDataUpdateRequest.of(saveDataResponse), false, TestJdyDo.class);
        Assert.equals(saveDataResponse.getDataId(), updateDataResponse.getDataId(), "数据不一致");

        // 根据数据ID查询数据
        final TestJdyDo getDataResponse = this.jdyClient.getData(JdyDataGetRequest.of(updateDataResponse), TestJdyDo.class);
        Assert.notNull(getDataResponse, "数据不存在");
        Assert.equals(getDataResponse.getDataId(), updateDataResponse.getDataId(), "数据不一致");

        // 批量查询
        final JdyListDataRequest jdyListDataRequest = JdyListDataRequest.of()
                .from(TestJdyDo.class)
                .eq(TestJdyDo::getStr, updateDataResponse.getStr());
        final List<TestJdyDo> listDataResponse = this.jdyClient.listData(jdyListDataRequest, TestJdyDo.class);
        Assert.isTrue(listDataResponse.size() == 1, "批量查询数量不一致, 测试失败");

        // 删除
        final boolean deleteDataResponse = this.jdyClient.deleteData(JdyDataDeleteRequest.of(getDataResponse));
        Assert.isTrue(deleteDataResponse, "删除失败");
    }

    /**
     * 测试批量
     */
    @Test
    void testBatch() {
        // 创建测试数据
        final List<TestJdyDo> testJdyDos = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            testJdyDos.add(this.createTestDo());
        }
        final List<String> dataIds = this.jdyClient.saveBatchData(JdyDataSaveBatchRequest.of(testJdyDos));
        Assert.isTrue(dataIds.size() == testJdyDos.size(), "批量新增数量不一致");
        log.info("批量新增数据: {}", dataIds.size());

        // 批量查询
        final JdyListDataRequest jdyListDataRequest = JdyListDataRequest.of()
                .from(TestJdyDo.class)
                .in(TestJdyDo::getStr, testJdyDos.stream().map(TestJdyDo::getStr).toList());
        final List<TestJdyDo> listDataResponse = this.jdyClient.listData(jdyListDataRequest, TestJdyDo.class);
        Assert.isTrue(listDataResponse.size() == dataIds.size(), "批量查询数量不一致");
        log.info("批量查询数据: {}", listDataResponse.size());

        // 修改测试数据
        final int updateSize = this.jdyClient.updateBatchData(JdyDataUpdateBatchRequest.of(this.createTestDo(), dataIds), false);
        Assert.isTrue(updateSize == dataIds.size(), "批量修改数量不一致");
        log.info("批量修改数据: {}", updateSize);

        // 删除
        final int deleteSize = this.jdyClient.deleteBatchData(JdyDataDeleteBatchRequest.of(listDataResponse));
        Assert.isTrue(deleteSize == listDataResponse.size(), "批量删除数量不一致");
        log.info("批量删除数据: {}", deleteSize);
    }

    /**
     * 创建测试数据
     */
    @Nonnull
    private TestJdyDo createTestDo() {
        // 子表单数据
        final Sub sub = new Sub();
        sub.setSubStr(IdUtil.fastSimpleUUID());
        sub.setSubNum(IdUtil.getSnowflakeNextId());
        sub.setSubTime(Instant.now());
        sub.setSubFile(null);

        // 子表单容器
        final JdySub<Sub> subs = new JdySub<>();
        for (int i = 0; i < 5; i++) {
            subs.add(sub);
        }

        // 主表数据
        final TestJdyDo testJdyDo = new TestJdyDo();
        testJdyDo.setStr(IdUtil.fastSimpleUUID());
        testJdyDo.setNum(IdUtil.getSnowflakeNextId());
        testJdyDo.setTime(Instant.now());
        testJdyDo.setFile(null);
        testJdyDo.setSub(subs);
        return testJdyDo;
    }

    @Test
    void testUploadFile() {
        // 测试数据
        final TestJdyDo testDo = this.createTestDo();

        // 上传文件
        final List<File> files = List.of(FileUtil.file("/Users/toint/Downloads/能力不足才华有限公司.png"),
                FileUtil.file("/Users/toint/Downloads/废话少说上号.jpeg"));
        final JdyFileUploadResponse jdyFileUploadResponse = this.jdyClient.uploadFile(JdyFileUploadRequest.of(testDo), files);

        // 赋值文件
        final JdyFile jdyFile = jdyFileUploadResponse.toJdyFile();
        testDo.setFile(jdyFile);
        testDo.getSub().forEach(sub -> sub.setSubFile(jdyFile));

        // 保存文件
        this.jdyClient.saveData(JdyDataSaveRequest.of(testDo).transactionId(jdyFileUploadResponse.getTransactionId()));
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