package cn.toint.jdy4j.core.client;

import cn.toint.jdy4j.core.constant.JdyConstant;
import cn.toint.jdy4j.core.event.JdyRequestEvent;
import cn.toint.jdy4j.core.exception.JdyRequestLimitException;
import cn.toint.tool.model.RetryPolicy;
import cn.toint.tool.util.HttpClientUtil;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.http.client.ClientConfig;
import org.dromara.hutool.http.client.engine.ClientEngine;
import org.dromara.hutool.http.client.engine.okhttp.OkHttpEngine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 简道云配置
 *
 * @author Toint
 * @date 2025/6/1
 */
@Data
public class JdyClientConfig {
    /**
     * 简道云服务地址, 私有化部署版本可替换本地址
     */
    @NotBlank
    private String url = JdyConstant.DEFAULT_SERVER_URL;

    /**
     * api key
     */
    @NotBlank
    private String apiKey;

    /**
     * http 客户端
     */
    @NotNull
    private ClientEngine clientEngine = this.defaultClientEngine();

    /**
     * 重试策略
     */
    @NotNull
    private List<RetryPolicy> retryPolicy = this.defaultRetryPolicies();

    /**
     * 请求信息异步回调, 开发者可用于记录日志等业务逻辑
     */
    @Nullable
    private Consumer<JdyRequestEvent> jdyRequestConsumer;

    public JdyClientConfig(final String apiKey) {
        Assert.notBlank(apiKey, "apiKey must not be blank");
        this.apiKey = apiKey;
    }

    /**
     * 默认客户端
     */
    private ClientEngine defaultClientEngine() {
        final ClientConfig clientConfig = ClientConfig.of()
                .setTimeout((int) Duration.ofSeconds(10).toMillis());
        return HttpClientUtil.clientEngine(OkHttpEngine.class, clientConfig);
    }

    /**
     * 默认重试策略
     */
    private List<RetryPolicy> defaultRetryPolicies() {
        final List<RetryPolicy> retryPolicies = new ArrayList<>();
        // 限流, 一直重试
        retryPolicies.add(new RetryPolicy(Integer.MAX_VALUE, Duration.ofSeconds(1), JdyRequestLimitException.class, true));
        // 其他异常, 重试3次
        retryPolicies.add(new RetryPolicy(3, Duration.ofSeconds(1), Exception.class, true));
        return retryPolicies;
    }
}