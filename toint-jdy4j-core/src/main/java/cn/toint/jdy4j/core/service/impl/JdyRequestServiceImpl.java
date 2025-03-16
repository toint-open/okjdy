package cn.toint.jdy4j.core.service.impl;

import cn.toint.jdy4j.core.model.JdyConfigStorage;
import cn.toint.jdy4j.core.service.JdyConfigStorageService;
import cn.toint.jdy4j.core.service.JdyRequestService;
import cn.toint.jdy4j.core.util.JacksonUtil;
import cn.toint.jdy4j.core.util.JdyConfigStorageHolder;
import cn.toint.jdy4j.core.util.JdyUrlUtil;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.thread.RetryableTask;
import org.dromara.hutool.http.client.ClientConfig;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.http.client.engine.ClientEngine;
import org.dromara.hutool.http.client.engine.ClientEngineFactory;
import org.dromara.hutool.http.client.engine.okhttp.OkHttpEngine;
import org.dromara.hutool.http.meta.HeaderName;

import java.time.Duration;

/**
 * @author Toint
 * @date 2024/10/19
 */
@Slf4j
public class JdyRequestServiceImpl implements JdyRequestService {
    /**
     * 简道云配置
     */
    @Resource
    private JdyConfigStorageService jdyConfigStorageService;

    /**
     * 简道云默认 http 客户端
     * 服务端3秒内未响应第一次内容则超时报错
     */
    private final ClientEngine clientEngine = ClientEngineFactory.createEngine(OkHttpEngine.class.getName())
            .init(ClientConfig.of().setTimeout(3000));

    @Override
    public JsonNode request(final Request request) {
        Assert.notNull(request, "request must not be null");
        // 获取当前持有者
        final JdyConfigStorage jdyConfigStorage = this.jdyConfigStorageService.getJdyConfigStorageRequire(JdyConfigStorageHolder.getRequire());
        Assert.notNull(jdyConfigStorage, "jdyConfigStorage must not be null");

        // 补充头信息
        final String apiKey = jdyConfigStorage.getApiKey();
        Assert.notBlank(apiKey, "apiKey must not be blank");
        request.header(HeaderName.AUTHORIZATION, "Bearer " + apiKey);

        // 匹配并替换为企业请求地址
        Assert.notNull(request.url(), "url must not be null");
        request.url(JdyUrlUtil.toCorpUrl(request.url().toString(), jdyConfigStorage.getServerUrl()));

        // 请求方法
        Assert.notNull(request.method(), "method must not be null");

        // 执行请求
        return RetryableTask.retryForExceptions(() -> this.doRequest(request), Exception.class)
                .maxAttempts(3)
                .delay(Duration.ofSeconds(1))
                .execute()
                .get()
                // hutool还不支持读取最后一次异常信息, 已经提了 pr, 暂时先用这个方案代替, 等于会执行4遍
                // https://gitee.com/chinabugotech/hutool/pulls/1316/files
                .orElseGet(() -> this.doRequest(request));
    }

    private JsonNode doRequest(final Request request) {
        try (final Response response = request.send(clientEngine)) {
            final String bodyStr = response.bodyStr();
            if (!response.isOk() || StringUtils.isBlank(bodyStr)) {
                final String msg = StrUtil.format("请求简道云服务器报错, status: {}, resBody: {}", response.getStatus(), bodyStr);
                throw new RuntimeException(msg);
            }
            // 执行到此说明一切正常, 除非简道云发癫返回的json有问题
            return JacksonUtil.readTree(bodyStr);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
