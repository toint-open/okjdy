package cn.toint.jdy4j.core.client.impl;

import cn.toint.jdy4j.core.client.JdyClient;
import cn.toint.jdy4j.core.client.JdyClientConfig;
import cn.toint.jdy4j.core.enums.JdyFieldTypeEnum;
import cn.toint.jdy4j.core.enums.JdyUrlEnum;
import cn.toint.jdy4j.core.event.JdyRequestEvent;
import cn.toint.jdy4j.core.exception.JdyRequestLimitException;
import cn.toint.jdy4j.core.model.*;
import cn.toint.jdy4j.core.util.JdyDataRequestConvertUtil;
import cn.toint.jdy4j.core.util.JdyHttpUtil;
import cn.toint.tool.util.Assert;
import cn.toint.tool.util.ExceptionUtil;
import cn.toint.tool.util.JacksonUtil;
import cn.toint.tool.util.RetryUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.core.date.TimeUtil;
import org.dromara.hutool.core.net.url.UrlBuilder;
import org.dromara.hutool.core.net.url.UrlQuery;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Toint
 * @date 2025/6/1
 */
@Slf4j
public class JdyClientImpl implements JdyClient {
    /**
     * 客户端配置
     */
    private final JdyClientConfig jdyClientConfig;

    public JdyClientImpl(@Nonnull final JdyClientConfig jdyClientConfig) {
        Assert.validate(jdyClientConfig, "jdyClient init error, cause: {}");
        this.jdyClientConfig = jdyClientConfig;
    }

    @Nonnull
    @Override
    public List<JdyApp> listApp(@Nonnull final JdyAppListRequest jdyAppListRequest) {
        Assert.notNull(jdyAppListRequest, "jdyAppRequest must not be null");

        // 单次最大数量
        final int onceSize = 100;
        final AtomicInteger limit = new AtomicInteger(jdyAppListRequest.getLimit());
        final AtomicInteger skip = new AtomicInteger(jdyAppListRequest.getSkip());
        final List<JdyApp> response = new ArrayList<>();

        while (true) {
            final JdyAppListRequest reqBody = new JdyAppListRequest();
            reqBody.setLimit(Math.min(limit.get(), onceSize));
            reqBody.setSkip(skip.get());

            final Request request = Request.of(JdyUrlEnum.LIST_APP.getUrl())
                    .method(JdyUrlEnum.LIST_APP.getMethod())
                    .body(JacksonUtil.writeValueAsString(reqBody));

            final String resBody = this.request(request);
            final List<JdyApp> apps = Optional.of(resBody)
                    .map(JacksonUtil::readTree)
                    .map(jsonNode -> jsonNode.path("apps"))
                    .filter(JsonNode::isArray)
                    .map(jsonNode -> JacksonUtil.treeToValue(jsonNode, new TypeReference<List<JdyApp>>() {
                    }))
                    .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));

            response.addAll(apps);
            limit.addAndGet(-apps.size());
            skip.addAndGet(apps.size());

            if (apps.size() < onceSize || limit.get() <= 0) {
                break;
            }
        }

        return response;
    }

    @Nonnull
    @Override
    public List<JdyEntry> listEntry(@Nonnull final JdyEntryListRequest jdyEntryListRequest) {
        Assert.validate(jdyEntryListRequest, "jdyEntryRequest valid error, cause: {}");

        // 单次最大数量
        final int onceSize = 100;
        final AtomicInteger limit = new AtomicInteger(jdyEntryListRequest.getLimit());
        final AtomicInteger skip = new AtomicInteger(jdyEntryListRequest.getSkip());
        final List<JdyEntry> response = new ArrayList<>();

        while (true) {
            final JdyEntryListRequest reqBody = new JdyEntryListRequest();
            reqBody.setAppId(jdyEntryListRequest.getAppId());
            reqBody.setLimit(Math.min(limit.get(), onceSize));
            reqBody.setSkip(skip.get());

            final Request request = Request.of(JdyUrlEnum.LIST_ENTRY.getUrl())
                    .method(JdyUrlEnum.LIST_ENTRY.getMethod())
                    .body(JacksonUtil.writeValueAsString(reqBody));

            final String resBody = this.request(request);
            final List<JdyEntry> forms = Optional.of(resBody)
                    .map(JacksonUtil::readTree)
                    .map(jsonNode -> jsonNode.path("forms"))
                    .filter(JsonNode::isArray)
                    .map(jsonNode -> JacksonUtil.treeToValue(jsonNode, new TypeReference<List<JdyEntry>>() {
                    }))
                    .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));

            response.addAll(forms);
            limit.addAndGet(-forms.size());
            skip.addAndGet(forms.size());

            if (forms.size() < onceSize || limit.get() <= 0) {
                break;
            }
        }

        return response;
    }

    @Override
    public @Nonnull JdyFieldListResponse listField(@Nonnull final JdyFieldListRequest jdyFieldListRequest) {
        Assert.validate(jdyFieldListRequest, "jdyFieldListRequest valid error, cause: {}");

        final Request request = Request.of(JdyUrlEnum.LIST_WIDGET.getUrl())
                .method(JdyUrlEnum.LIST_WIDGET.getMethod())
                .body(JacksonUtil.writeValueAsString(jdyFieldListRequest));

        final String resBody = this.request(request);
        final JdyFieldListResponse jdyFieldListResponse = Optional.of(resBody)
                .map(str -> JacksonUtil.readValue(str, JdyFieldListResponse.class))
                .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));
        Assert.validate(jdyFieldListRequest, "简道云响应异常, body: {}, cause: {}", resBody);
        return jdyFieldListResponse;
    }

    @Nonnull
    @Override
    public JsonNode getData(@Nonnull final JdyDataGetRequest jdyDataGetRequest) {
        Assert.validate(jdyDataGetRequest, "jdyDataGetRequest valid error, cause: {}");

        final Request request = Request.of(JdyUrlEnum.GET_DATA.getUrl())
                .method(JdyUrlEnum.GET_DATA.getMethod())
                .body(JacksonUtil.writeValueAsString(jdyDataGetRequest));

        final String resBody = this.request(request);
        return Optional.of(resBody)
                .map(JacksonUtil::readTree)
                .map(jsonNode -> jsonNode.path("data"))
                .filter(JacksonUtil::isNotNull)
                .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));
    }

    @Nonnull
    @Override
    public <T extends JdyDo> T getData(@Nonnull final JdyDataGetRequest jdyDataGetRequest, final @Nonnull Class<T> responseClass) {
        Assert.notNull(responseClass, "responseClass must not be null");
        return JacksonUtil.treeToValue(this.getData(jdyDataGetRequest), responseClass);
    }

    @Override
    public @Nonnull JsonNode listData(final @Nonnull JdyListDataRequest jdyListDataRequest) {
        return this.listData(jdyListDataRequest, jsonNode -> true);
    }

    @Nonnull
    @Override
    public <T extends JdyDo> List<T> listData(@Nonnull final JdyListDataRequest jdyListDataRequest, @Nonnull final Class<T> responseType) {
        return this.listData(jdyListDataRequest, responseType, jsonNode -> true);
    }

    @Override
    @Nonnull
    public JsonNode listData(final @Nonnull JdyListDataRequest jdyListDataRequest, final @Nullable Predicate<JsonNode> predicate) {
        Assert.validate(jdyListDataRequest, "jdyListRequest valid error, cause: {}");

        // 转换字段, 数字和字符串需要严格区分, 根据简道云字段类型判断
        this.convertConditionFieldValue(jdyListDataRequest);

        // 单次最大数量
        final int onceSize = 100;
        final AtomicInteger limit = new AtomicInteger(jdyListDataRequest.getLimit());
        final ArrayNode response = JacksonUtil.createArrayNode();
        String dataId = jdyListDataRequest.getDataId();

        while (true) {
            final JdyListDataRequest reqBody = new JdyListDataRequest();
            reqBody.setAppId(jdyListDataRequest.getAppId());
            reqBody.setEntryId(jdyListDataRequest.getEntryId());
            reqBody.setDataId(dataId);
            reqBody.setFields(jdyListDataRequest.getFields());
            reqBody.setFilter(jdyListDataRequest.getFilter());
            reqBody.setLimit(Math.min(limit.get(), onceSize));

            final Request request = Request.of(JdyUrlEnum.LIST_DATA.getUrl())
                    .method(JdyUrlEnum.LIST_DATA.getMethod())
                    .body(JacksonUtil.writeValueAsString(reqBody));

            final String resBody = this.request(request);
            final JsonNode data = Optional.of(resBody)
                    .map(JacksonUtil::readTree)
                    .map(jsonNode -> jsonNode.path("data"))
                    .filter(JsonNode::isArray)
                    .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));

            // 可在回调中控制是否过滤数据, 避免数据量过大撑爆内存
            // 捕获异常, 避免回调方法异常导致整个任务失败
            // 异常发生后, 会忽略结果
            try {
                if (!JacksonUtil.isEmpty(data) && (predicate == null || predicate.test(data))) {
                    final ArrayNode arr = data.deepCopy();
                    response.addAll(arr);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            // 扣减数量
            limit.addAndGet(-data.size());

            // 退出方法
            final String lastDataId = this.getLastDataId(data);
            if (data.size() < onceSize || limit.get() <= 0 || StringUtils.isBlank(lastDataId)) {
                break;
            } else {
                dataId = lastDataId;
            }

        }

        return response;
    }

    @Nonnull
    @Override
    public <T extends JdyDo> List<T> listData(@Nonnull final JdyListDataRequest jdyListDataRequest, @Nonnull final Class<T> responseType, @Nullable final Predicate<JsonNode> predicate) {
        Assert.notNull(responseType, "responseType must not be null");
        final List<T> response = new ArrayList<>();
        for (final JsonNode item : this.listData(jdyListDataRequest, predicate)) {
            response.add(JacksonUtil.treeToValue(item, responseType));
        }
        return response;
    }

    @Nonnull
    @Override
    public JsonNode save(@Nonnull final JdyDataSaveRequest jdyDataSaveRequest) {
        Assert.validate(jdyDataSaveRequest, "jdyDataSaveRequest valid error, cause: {}");

        // 转换 data
        final JdyFieldListResponse jdyFieldListResponse = this.listField(JdyFieldListRequest.of(jdyDataSaveRequest.getData()));
        final JsonNode newData = JdyDataRequestConvertUtil.convert(jdyDataSaveRequest.getData(), jdyFieldListResponse.getWidgets());
        jdyDataSaveRequest.setData(newData);

        final Request request = Request.of(JdyUrlEnum.SAVE_ONE_DATA.getUrl())
                .method(JdyUrlEnum.SAVE_ONE_DATA.getMethod())
                .body(JacksonUtil.writeValueAsString(jdyDataSaveRequest));

        final String resBody = this.request(request);
        return Optional.of(resBody)
                .map(JacksonUtil::readTree)
                .map(jsonNode -> jsonNode.path("data"))
                .filter(JacksonUtil::isNotNull)
                .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));
    }

    @Nonnull
    @Override
    public <T> T save(@Nonnull final JdyDataSaveRequest jdyDataSaveRequest, final @Nonnull Class<T> responseClass) {
        return JacksonUtil.treeToValue(this.save(jdyDataSaveRequest), responseClass);
    }

    @Nonnull
    @Override
    public String request(@Nonnull final Request request) {
        Assert.notNull(request, "request must not be null");

        // 替换 url
        UrlBuilder url = request.url();
        final String pathStr = url.getPathStr();
        final UrlQuery query = url.getQuery();
        Assert.notNull(url, "url must not be null");
        Assert.notBlank(pathStr, "path must not be blank");
        url = UrlBuilder.ofHttp(this.jdyClientConfig.getUrl())
                .addPath(pathStr)
                .setQuery(query);
        request.url(url);

        // apikey
        request.header(HttpHeaders.AUTHORIZATION, "Bearer " + this.jdyClientConfig.getApiKey());

        // 执行请求并重试
        return RetryUtil.execute(() -> this.executeRequest(request), this.jdyClientConfig.getRetryPolicy());
    }

    @Nonnull
    private String executeRequest(final @Nonnull Request request) throws IOException {
        // 简道云所有 API 使用状态码 + 错误码的响应方式来表示错误原因。
        // 接口正确统一返回HTTP 状态码为 2xx 的正确响应。
        // 接口错误则统一返回 HTTP 状态码为 400 的错误响应，同时响应内容会返回错误码（code）和错误信息（msg）

        String bodyStr = null;
        Integer status = null;
        Map<String, List<String>> headers = null;
        final LocalDateTime startTime = LocalDateTime.now();
        try (final Response response = this.jdyClientConfig.getClientEngine().send(request)) {
            bodyStr = response.bodyStr();
            headers = response.headers();
            status = response.getStatus();

            // 限流异常
            if (JdyHttpUtil.isLimitException(bodyStr)) {
                throw new JdyRequestLimitException(StrUtil.format("简道云接口超出频率限制, status: {}, body: {}", status, bodyStr));
            }

            // 其他异常
            if (!response.isOk() || StringUtils.isBlank(bodyStr)) {
                throw new RuntimeException(StrUtil.format("简道云响应异常, status: {}, body: {}", status, bodyStr));
            }

            return bodyStr;
        } finally {
            // 异步回调
            if (this.jdyClientConfig.getJdyRequestConsumer() != null) {
                final JdyRequestEvent.RequestInfo requestInfo = new JdyRequestEvent.RequestInfo();
                requestInfo.setUrl(request.url() == null ? null : request.url().build());
                requestInfo.setMethod(request.method() == null ? null : request.method().name());
                requestInfo.setRequestBody(request.bodyStr());
                requestInfo.setRequestHeader(request.headers());
                requestInfo.setRequestTime(startTime);
                requestInfo.setResponseBody(bodyStr);
                requestInfo.setResponseHeader(headers);
                requestInfo.setStatus(status);
                requestInfo.setResponseTime(LocalDateTime.now());
                requestInfo.setDurationTime(TimeUtil.between(requestInfo.getRequestTime(), requestInfo.getResponseTime(), ChronoUnit.MILLIS));
                Thread.startVirtualThread(() -> this.jdyClientConfig.getJdyRequestConsumer().accept(new JdyRequestEvent(requestInfo)));
            }
        }
    }

    /**
     * 获取集合最后一条数据编号
     *
     * @param jsonNode 数据集合, 示例: [{数据1}, {数据2}]
     * @return 最后一条数据编号, 获取失败则返回 null
     */
    @Nullable
    private String getLastDataId(@Nonnull JsonNode jsonNode) {
        if (JacksonUtil.isEmpty(jsonNode) || !jsonNode.isArray()) {
            return null;
        }

        final int index = jsonNode.size() - 1;
        return jsonNode.path(index)
                .path("_id")
                .asText(null);
    }

    // ====

    /**
     * 转换字段, 数字和字符串需要严格区分, 根据简道云字段类型判断
     */
    private void convertConditionFieldValue(@Nonnull JdyListDataRequest jdyListDataRequest) {
        final Collection<JdyCondition> conditions = jdyListDataRequest.getFilter().getCondition();

        // 只要 value 有值, 就获取字段列表进行转换字段值
        if (CollUtil.isEmpty(conditions) || conditions.stream().map(JdyCondition::getValue).noneMatch(Objects::nonNull)) {
            return;
        }

        // 获取字段列表
        final JdyFieldListRequest jdyFieldListRequest = new JdyFieldListRequest();
        jdyFieldListRequest.setAppId(jdyListDataRequest.getAppId());
        jdyFieldListRequest.setEntryId(jdyListDataRequest.getEntryId());
        final JdyFieldListResponse jdyFieldListResponse = this.listField(jdyFieldListRequest);

        // 字段列表
        final List<JdyField> fields = jdyFieldListResponse.getWidgets();
        if (CollUtil.isEmpty(fields)) {
            return;
        }

        // key: name, value: type
        final Map<String, String> nameTypeMap = fields.stream().collect(Collectors.toMap(JdyField::getName, JdyField::getType));

        for (final JdyCondition condition : conditions) {
            final String fieldName = condition.getField();
            final Collection<Object> values = condition.getValue();

            // 设置type
            final String type = nameTypeMap.get(fieldName);
            condition.setType(type);

            if (CollUtil.isEmpty(values)) {
                continue;
            }

            // 执行转换, 数字字段转数字, 其他字段一律转字符串
            final Collection<Object> newValue = new ArrayList<>();
            for (final Object value : values) {
                if (JdyFieldTypeEnum.NUMBER.getValue().equals(type)) {
                    final Number number = ConvertUtil.toNumber(value);
                    Assert.notNull(number, "value convert to number error");
                    newValue.add(number);
                } else {
                    // 可以是空字符串, 但是不能是 null
                    final String str = ConvertUtil.toStr(value);
                    Assert.notNull(str, "value convert to str error");
                    newValue.add(str);
                }
            }
            condition.setValue(newValue);
        }
    }
}
