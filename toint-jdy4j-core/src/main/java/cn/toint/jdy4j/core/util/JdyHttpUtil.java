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

package cn.toint.jdy4j.core.util;

import cn.toint.jdy4j.core.exception.JdyRequestLimitException;
import cn.toint.tool.model.RetryPolicy;
import cn.toint.tool.util.HttpClientUtil;
import cn.toint.tool.util.JacksonUtil;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.http.client.ClientConfig;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.http.client.engine.ClientEngine;
import org.dromara.hutool.http.client.engine.okhttp.OkHttpEngine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Toint
 * @date 2025/3/17
 */
public class JdyHttpUtil {
    /**
     * 简道云默认 http 客户端
     * 服务端10秒内未响应第一次内容则超时报错
     */
    private static final ClientEngine CLIENT_ENGINE = HttpClientUtil.clientEngine(OkHttpEngine.class, ClientConfig.of().setTimeout(10000));

    /**
     * 重试策略, 如有自定义需求可替换策略
     */
    private static List<RetryPolicy> retryPolicies = JdyHttpUtil.defaultRetryPolicies();

    public static ClientEngine getClientEngine() {
        return JdyHttpUtil.CLIENT_ENGINE;
    }

    public static Response request(final Request request) {
        return JdyHttpUtil.CLIENT_ENGINE.send(request);
    }

    /**
     * 是否为请求超过频率异常
     *
     * @param responseBody responseBody
     * @return 是否为请求超过频率异常
     */
    public static boolean isLimitException(final String responseBody) {
        return Optional.ofNullable(responseBody)
                .filter(StringUtils::isNotBlank)
                .map(JacksonUtil::tryReadTree)
                .map(jsonNode -> jsonNode.path("code"))
                .map(code -> code.asInt(-1))
                .filter(code -> code == 8303 || code == 8304) // 请求超过频率异常
                .isPresent();
    }

    public static List<RetryPolicy> retryPolicies() {
        if (CollUtil.isEmpty(JdyHttpUtil.retryPolicies)) {
            JdyHttpUtil.retryPolicies = JdyHttpUtil.defaultRetryPolicies();
        }
        return JdyHttpUtil.retryPolicies;
    }

    public static void retryPolicies(final List<RetryPolicy> retryPolicies) {
        if (CollUtil.isEmpty(retryPolicies)) {
            JdyHttpUtil.retryPolicies = JdyHttpUtil.defaultRetryPolicies();
        } else {
            JdyHttpUtil.retryPolicies = retryPolicies;
        }
    }

    /**
     * 默认重试策略
     */
    private static List<RetryPolicy> defaultRetryPolicies() {
        final List<RetryPolicy> retryPolicies = new ArrayList<>();
        // 限流, 一直重试
        retryPolicies.add(new RetryPolicy(Integer.MAX_VALUE, Duration.ofSeconds(1), JdyRequestLimitException.class, true));
        // 其他异常, 重试3次
        retryPolicies.add(new RetryPolicy(3, Duration.ofSeconds(1), Exception.class, true));
        return retryPolicies;
    }
}
