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

package cn.toint.jdy4j.core.service.impl;

import cn.toint.jdy4j.core.event.JdyRequestEvent;
import cn.toint.jdy4j.core.exception.JdyRequestLimitException;
import cn.toint.jdy4j.core.model.JdyConfigStorage;
import cn.toint.jdy4j.core.service.JdyConfigStorageService;
import cn.toint.jdy4j.core.service.JdyRequestService;
import cn.toint.jdy4j.core.util.JdyHttpUtil;
import cn.toint.tool.util.JacksonUtil;
import cn.toint.jdy4j.core.util.JdyConfigStorageHolder;
import cn.toint.jdy4j.core.util.JdyUrlUtil;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.date.TimeUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.http.meta.HeaderName;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

        int maxRetrySize = 3;
        while (true) {
            try {
                return this.doRequest(request);
            } catch (JdyRequestLimitException e) {
                // 接口超出频率, 进入重试机制
                log.error(e.getMessage(), e);
                maxRetrySize--;
                if (maxRetrySize == 0) {
                    throw e;
                }
            } catch (Exception e) {
                // 其他异常, 直接返回
                log.error(e.getMessage(), e);
                throw e;
            }
        }
    }

    /**
     * 执行请求
     *
     * @param request 请求信息, 方法内不会对请求信息做任何修改
     * @return 请求原始结果
     * @throws JdyRequestLimitException 超出频率
     * @throws RuntimeException 任何请求异常, 都会抛出异常
     */
    private @NonNull JsonNode doRequest(@NonNull final Request request) {
        final JdyRequestEvent.RequestInfo requestInfo = new JdyRequestEvent.RequestInfo();
        requestInfo.setUrl(request.url() == null ? null : request.url().build());
        requestInfo.setMethod(request.method() == null ? null : request.method().name());
        requestInfo.setRequestBody(request.bodyStr());
        requestInfo.setRequestHeader(request.headers());
        requestInfo.setRequestTime(LocalDateTime.now());

        try (final Response response = request.send(JdyHttpUtil.getClientEngine())) {
            // 简道云所有 API 使用状态码 + 错误码的响应方式来表示错误原因。
            // 接口正确统一返回HTTP 状态码为 2xx 的正确响应。
            // 接口错误则统一返回 HTTP 状态码为 400 的错误响应，同时响应内容会返回错误码（code）和错误信息（msg）
            final String bodyStr = response.bodyStr();
            requestInfo.setResponseBody(bodyStr);
            requestInfo.setResponseHeader(response.headers());
            requestInfo.setStatus(response.getStatus());
            requestInfo.setResponseTime(LocalDateTime.now());
            requestInfo.setDurationTime(TimeUtil.between(requestInfo.getRequestTime(), requestInfo.getResponseTime(), ChronoUnit.MILLIS));

            // 超出频率异常
            if (JdyHttpUtil.isLimitException(response.getStatus(), bodyStr)) {
                final String errMsg = StrUtil.format("简道云接口超出频率限制, status: {}, body: {}", response.getStatus(), bodyStr);
                throw new JdyRequestLimitException(errMsg);
            }

            // 其他异常
            if (!response.isOk() || StringUtils.isBlank(bodyStr)) {
                final String errMsg = StrUtil.format("简道云请求异常, status: {}, body: {}", response.getStatus(), bodyStr);
                throw new RuntimeException(errMsg);
            }

            // 执行到此说明一切正常, 除非简道云发癫返回的 json 有问题
            return JacksonUtil.readTree(bodyStr);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            SpringUtil.publishEvent(new JdyRequestEvent(requestInfo));
        }
    }
}