/*
 * Copyright 2025 Toint (599818663@qq.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.toint.okjdy.core.config;

import cn.toint.okjdy.core.constant.JdyConstant;
import cn.toint.okjdy.core.event.JdyRequestEvent;
import cn.toint.okjdy.core.exception.JdyRequestLimitException;
import cn.toint.oktool.model.RetryPolicy;
import cn.toint.oktool.util.HttpClientUtil;
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