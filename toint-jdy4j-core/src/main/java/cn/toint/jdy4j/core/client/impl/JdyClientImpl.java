package cn.toint.jdy4j.core.client.impl;

import cn.toint.jdy4j.core.client.JdyClient;
import cn.toint.jdy4j.core.config.JdyClientConfig;
import cn.toint.jdy4j.core.enums.JdyFieldTypeEnum;
import cn.toint.jdy4j.core.enums.JdyUrlEnum;
import cn.toint.jdy4j.core.event.JdyRequestEvent;
import cn.toint.jdy4j.core.exception.JdyRequestLimitException;
import cn.toint.jdy4j.core.model.*;
import cn.toint.jdy4j.core.util.JdyDataRequestConvertUtil;
import cn.toint.jdy4j.core.util.JdyHttpUtil;
import cn.toint.tool.exception.RetryException;
import cn.toint.tool.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.cache.CacheUtil;
import org.dromara.hutool.core.cache.impl.TimedCache;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.core.date.TimeUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.net.url.UrlBuilder;
import org.dromara.hutool.core.net.url.UrlQuery;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.http.client.body.MultipartBody;
import org.dromara.hutool.http.meta.Method;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
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

    @SuppressWarnings("deprecation")
    private final List<String> whiteContentType = List.of(MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE);

    /**
     * 字段缓存, 避免频繁访问 api 引发瓶颈
     * 缓存30s, 30s 过后自动清除缓存, 直至下一次该表单被再次查询
     */
    private final TimedCache<String, JdyFieldListResponse> fieldCache = CacheUtil.newTimedCache(Duration.ofSeconds(30).toMillis(), Duration.ofSeconds(30).toMillis());

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

        // 先从缓存找, 缓存不存在再调用 api
        final String key = KeyBuilderUtil.of(jdyFieldListRequest.getAppId()).build(jdyFieldListRequest.getEntryId());
        final JdyFieldListResponse responseByCache = this.fieldCache.get(key, false);
        if (responseByCache != null) {
            return responseByCache;
        }

        final Request request = Request.of(JdyUrlEnum.LIST_WIDGET.getUrl())
                .method(JdyUrlEnum.LIST_WIDGET.getMethod())
                .body(JacksonUtil.writeValueAsString(jdyFieldListRequest));

        final String resBody = this.request(request);
        final JdyFieldListResponse jdyFieldListResponse = Optional.of(resBody)
                .map(str -> JacksonUtil.readValue(str, JdyFieldListResponse.class))
                .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));
        Assert.validate(jdyFieldListResponse, "简道云响应异常, body: {}, cause: {}", resBody);
        this.fieldCache.put(key, jdyFieldListResponse);
        return jdyFieldListResponse;
    }

    @Override
    public @Nullable JsonNode getData(@Nonnull final JdyDataGetRequest jdyDataGetRequest) {
        Assert.validate(jdyDataGetRequest, "jdyDataGetRequest valid error, cause: {}");

        // 请求参数
        final Request request = Request.of(JdyUrlEnum.GET_DATA.getUrl())
                .method(JdyUrlEnum.GET_DATA.getMethod())
                .body(JacksonUtil.writeValueAsString(jdyDataGetRequest));

        // 执行请求
        final String resBody = this.request(request);
        return Optional.of(resBody)
                .map(JacksonUtil::readTree)
                .map(jsonNode -> jsonNode.get("data"))
                .orElse(null);
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

        while (true) {
            final JdyListDataRequest reqBody = new JdyListDataRequest();
            reqBody.setAppId(jdyListDataRequest.getAppId());
            reqBody.setEntryId(jdyListDataRequest.getEntryId());
            reqBody.setDataId(jdyListDataRequest.getDataId());
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
                jdyListDataRequest.setDataId(lastDataId);
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
    public JsonNode saveData(@Nonnull final JdyDataSaveRequest jdyDataSaveRequest) {
        Assert.validate(jdyDataSaveRequest, "jdyDataSaveRequest valid error, cause: {}");

        // 转换 data
        final JdyFieldListResponse jdyFieldListResponse = this.listField(JdyFieldListRequest.of(jdyDataSaveRequest.getData()));
        final JsonNode newData = JdyDataRequestConvertUtil.convert(jdyDataSaveRequest.getData(), jdyFieldListResponse.getWidgets());
        jdyDataSaveRequest.setData(newData);

        // 请求参数
        final Request request = Request.of(JdyUrlEnum.SAVE_ONE_DATA.getUrl())
                .method(JdyUrlEnum.SAVE_ONE_DATA.getMethod())
                .body(JacksonUtil.writeValueAsString(jdyDataSaveRequest));

        // 执行请求
        final String resBody = this.request(request);
        return Optional.of(resBody)
                .map(JacksonUtil::readTree)
                .map(jsonNode -> jsonNode.path("data"))
                .filter(jsonNode -> StringUtils.isNotBlank(jsonNode.path("_id").asText()))
                .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));
    }

    @Nonnull
    @Override
    public <T> T saveData(@Nonnull final JdyDataSaveRequest jdyDataSaveRequest, final @Nonnull Class<T> responseClass) {
        return JacksonUtil.treeToValue(this.saveData(jdyDataSaveRequest), responseClass);
    }

    @Nonnull
    @Override
    public List<String> saveBatchData(@Nonnull final JdyDataSaveBatchRequest jdyDataSaveBatchRequest) {
        Assert.validate(jdyDataSaveBatchRequest, "jdyDataSaveRequest valid error, cause: {}");

        // 待保存的数据列表
        final JsonNode datas = jdyDataSaveBatchRequest.getDatas();
        Assert.notEmpty(datas, "datas must not be empty");
        Assert.isTrue(datas.isArray(), "datas must be array");

        // 字段信息
        final JdyFieldListResponse jdyFieldListResponse = this.listField(JdyFieldListRequest.of(datas.path(0)));

        // 新数据列表
        final ArrayList<JsonNode> newDatas = new ArrayList<>();
        for (final JsonNode item : datas) {
            final JsonNode newData = JdyDataRequestConvertUtil.convert(item, jdyFieldListResponse.getWidgets());
            newDatas.add(newData);
        }

        // 每次保存100条
        final List<String> successIds = new ArrayList<>();
        for (final List<JsonNode> jsonNodes : CollUtil.partition(newDatas, 100)) {
            jdyDataSaveBatchRequest.setDatas(JacksonUtil.valueToTree(jsonNodes));
            final Request request = Request.of(JdyUrlEnum.SAVE_BATCH_DATA.getUrl())
                    .method(JdyUrlEnum.SAVE_BATCH_DATA.getMethod())
                    .body(JacksonUtil.writeValueAsString(jdyDataSaveBatchRequest));

            final String resBody = this.request(request);
            final List<String> ids = Optional.of(resBody)
                    .map(JacksonUtil::readTree)
                    .map(jsonNode -> jsonNode.path("success_ids"))
                    .filter(JsonNode::isArray)
                    .map(jsonNode -> JacksonUtil.treeToValue(jsonNode, new TypeReference<List<String>>() {
                    }))
                    .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));
            successIds.addAll(ids);
        }
        return successIds;
    }

    @Nonnull
    @Override
    public JsonNode updateData(@Nonnull final JdyDataUpdateRequest jdyDataUpdateRequest, final boolean ignoreNull) {
        Assert.validate(jdyDataUpdateRequest, "jdyDataUpdateRequest valid error, cause: {}");

        // 简道云字段保持原值
        if (ignoreNull) {
            final Iterator<JsonNode> iterator = jdyDataUpdateRequest.getData().iterator();
            while (iterator.hasNext()) {
                if (JacksonUtil.isNull(iterator.next())) {
                    iterator.remove();
                }
            }
        }

        // 转换 data
        final JdyFieldListResponse jdyFieldListResponse = this.listField(JdyFieldListRequest.of(jdyDataUpdateRequest.getData()));
        final JsonNode newData = JdyDataRequestConvertUtil.convert(jdyDataUpdateRequest.getData(), jdyFieldListResponse.getWidgets());
        jdyDataUpdateRequest.setData(newData);

        // 请求参数
        final Request request = Request.of(JdyUrlEnum.UPDATE_ONE_DATA.getUrl())
                .method(JdyUrlEnum.UPDATE_ONE_DATA.getMethod())
                .body(JacksonUtil.writeValueAsString(jdyDataUpdateRequest));

        // 执行请求
        final String resBody = this.request(request);
        return Optional.of(resBody)
                .map(JacksonUtil::readTree)
                .map(jsonNode -> jsonNode.path("data"))
                .filter(jsonNode -> StringUtils.isNotBlank(jsonNode.path("_id").asText()))
                .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));
    }

    @Nonnull
    @Override
    public <T extends JdyDo> T updateData(@Nonnull final JdyDataUpdateRequest jdyDataUpdateRequest, final boolean ignoreNull, @Nonnull final Class<T> responseType) {
        Assert.notNull(responseType, "responseType must not be null");
        return JacksonUtil.treeToValue(this.updateData(jdyDataUpdateRequest, ignoreNull), responseType);
    }

    @Override
    public int updateBatchData(@Nonnull final JdyDataUpdateBatchRequest jdyDataUpdateBatchRequest, final boolean ignoreNull) {
        Assert.validate(jdyDataUpdateBatchRequest, "jdyDataUpdateBatchRequest valid error, cause: {}");

        // 忽略 null
        if (ignoreNull) {
            final Iterator<JsonNode> iterator = jdyDataUpdateBatchRequest.getData().iterator();
            while (iterator.hasNext()) {
                if (JacksonUtil.isNull(iterator.next())) {
                    iterator.remove();
                }
            }
        }

        // 转换 data
        final JdyFieldListResponse jdyFieldListResponse = this.listField(JdyFieldListRequest.of(jdyDataUpdateBatchRequest.getData()));
        final JsonNode newData = JdyDataRequestConvertUtil.convert(jdyDataUpdateBatchRequest.getData(), jdyFieldListResponse.getWidgets());
        jdyDataUpdateBatchRequest.setData(newData);

        final AtomicInteger successCount = new AtomicInteger();
        CollUtil.partition(jdyDataUpdateBatchRequest.getDataIds(), 100).forEach(dataIds -> {
            jdyDataUpdateBatchRequest.setDataIds(dataIds);
            final Request request = Request.of(JdyUrlEnum.UPDATE_BATCH_DATA.getUrl())
                    .method(JdyUrlEnum.UPDATE_BATCH_DATA.getMethod())
                    .body(JacksonUtil.writeValueAsString(jdyDataUpdateBatchRequest));

            final String resBody = this.request(request);
            final int count = Optional.of(resBody)
                    .map(JacksonUtil::readTree)
                    .map(jsonNode -> jsonNode.path("success_count").asInt(-1))
                    .filter(num -> num >= 0)
                    .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));
            successCount.addAndGet(count);
        });

        return successCount.get();
    }

    @Override
    public boolean deleteData(@Nonnull final JdyDataDeleteRequest jdyDataDeleteRequest) {
        Assert.validate(jdyDataDeleteRequest, "jdyDataDeleteRequest valid error, cause: {}");

        // 请求参数
        final Request request = Request.of(JdyUrlEnum.DELETE_ONE_DATA.getUrl())
                .method(JdyUrlEnum.DELETE_ONE_DATA.getMethod())
                .body(JacksonUtil.writeValueAsString(jdyDataDeleteRequest));

        // 执行请求, 若数据不存在会抛异常
        final String resBody;
        try {
            resBody = this.request(request);
        } catch (Exception e) {
            return false;
        }

        // 读取响应
        return Optional.of(resBody)
                .map(JacksonUtil::readTree)
                .map(jsonNode -> jsonNode.path("status").asText())
                .filter(str -> Objects.equals("success", str))
                .isPresent();
    }

    @Override
    public int deleteBatchData(@Nonnull final JdyDataDeleteBatchRequest jdyDataDeleteBatchRequest) {
        Assert.validate(jdyDataDeleteBatchRequest, "jdyDataDeleteBatchRequest valid error, cause: {}");

        final AtomicInteger successCount = new AtomicInteger();
        CollUtil.partition(jdyDataDeleteBatchRequest.getDataIds(), 100).forEach(dataIds -> {
            jdyDataDeleteBatchRequest.setDataIds(dataIds);
            final Request request = Request.of(JdyUrlEnum.DELETE_BATCH_DATA.getUrl())
                    .method(JdyUrlEnum.DELETE_BATCH_DATA.getMethod())
                    .body(JacksonUtil.writeValueAsString(jdyDataDeleteBatchRequest));

            final String resBody = this.request(request);
            final int count = Optional.of(resBody)
                    .map(JacksonUtil::readTree)
                    .map(jsonNode -> jsonNode.path("success_count").asInt())
                    .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));
            successCount.addAndGet(count);
        });
        return successCount.get();
    }

    @Nonnull
    @Override
    public JdyFileUploadResponse uploadFile(@Nonnull final JdyFileUploadRequest jdyFileUploadRequest, @Nonnull final Collection<File> files) {
        Assert.validate(jdyFileUploadRequest, "jdyFileUploadRequest valid error, cause: {}");
        Assert.notEmpty(files, "files must not be empty");

        // 文件必须存在才可以上传
        final Set<File> fileSet = files.stream().filter(FileUtil::exists).collect(Collectors.toSet());
        Assert.notEmpty(fileSet, "files must not be empty");

        // 获取足量的文件上传凭证和上传地址
        final Deque<JdyFileGetUploadTokenResponse> tokenAndUrls = new ArrayDeque<>();
        final Request request = Request.of(JdyUrlEnum.GET_UPLOAD_TOKEN.getUrl())
                .method(JdyUrlEnum.GET_UPLOAD_TOKEN.getMethod())
                .body(JacksonUtil.writeValueAsString(jdyFileUploadRequest));
        do {
            final String resBody = this.request(request);
            final List<JdyFileGetUploadTokenResponse> getUploadTokenResponses = Optional.of(resBody)
                    .map(JacksonUtil::readTree)
                    .map(jsonNode -> jsonNode.path("token_and_url_list"))
                    .filter(JsonNode::isArray)
                    .map(jsonNode -> JacksonUtil.treeToValue(jsonNode, new TypeReference<List<JdyFileGetUploadTokenResponse>>() {
                    }))
                    .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));
            getUploadTokenResponses.forEach(item -> Assert.validate(item, "jdyGetUploadTokenResponse valid error, cause: {}"));
            tokenAndUrls.addAll(getUploadTokenResponses);
        } while (tokenAndUrls.size() < fileSet.size());

        // 上传文件
        final JdyFileUploadResponse jdyFileUploadResponse = new JdyFileUploadResponse();
        jdyFileUploadResponse.setTransactionId(jdyFileUploadRequest.getTransactionId());
        for (final File file : fileSet) {
            final JdyFileGetUploadTokenResponse tokenAndUrl = tokenAndUrls.pop();

            final HashMap<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("token", tokenAndUrl.getToken());
            bodyMap.put("file", file);

            final Request uploadFileRequest = Request.of(tokenAndUrl.getUrl())
                    .method(Method.POST)
                    .body(MultipartBody.of(bodyMap, StandardCharsets.UTF_8));
            // 执行请求并重试
            final String resBody = RetryUtil.execute(() -> this.executeRequest(uploadFileRequest), this.jdyClientConfig.getRetryPolicy());
            final String key = Optional.of(resBody)
                    .map(JacksonUtil::readTree)
                    .map(jsonNode -> jsonNode.path("key").asText())
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> ExceptionUtil.wrapRuntimeException("简道云响应异常, body: {}", resBody));
            jdyFileUploadResponse.getFileKeyMap().put(file, key);
        }

        return jdyFileUploadResponse;
    }

    /**
     * 请求简道云
     *
     * @param request request
     * @return 响应体
     * @throws RetryException 请求重试后仍然异常
     */
    @Nonnull
    private String request(@Nonnull final Request request) {
        Assert.notNull(request, "request must not be null");
        Assert.validate(this.jdyClientConfig, "jdyClientConfig valid error, cause: {}");

        // 替换 url
        Assert.notNull(request.url(), "url must not be null");
        final String pathStr = request.url().getPathStr();
        final UrlQuery query = request.url().getQuery();
        Assert.notBlank(pathStr, "path must not be blank");
        request.url(UrlBuilder.ofHttp(this.jdyClientConfig.getUrl())
                .addPath(pathStr)
                .setQuery(query));

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
            if (StringUtils.isBlank(bodyStr)) {
                throw new RuntimeException(StrUtil.format("简道云响应异常, status: {}, body: {}", status, bodyStr));
            }

            return bodyStr;
        } finally {
            // 异步回调
            if (this.jdyClientConfig.getJdyRequestConsumer() != null) {
                final JdyRequestEvent.RequestInfo requestInfo = new JdyRequestEvent.RequestInfo();
                requestInfo.setUrl(request.url() == null ? null : request.url().build());
                requestInfo.setMethod(request.method() == null ? null : request.method().name());
                requestInfo.setRequestHeader(request.headers());
                requestInfo.setRequestTime(startTime);
                requestInfo.setResponseBody(bodyStr);
                requestInfo.setResponseHeader(headers);
                requestInfo.setStatus(status);
                requestInfo.setResponseTime(LocalDateTime.now());
                requestInfo.setDurationTime(TimeUtil.between(requestInfo.getRequestTime(), requestInfo.getResponseTime(), ChronoUnit.MILLIS));

                // 过滤请求 body 日志
                final String contentType = request.header(HttpHeaders.CONTENT_TYPE);
                if (StringUtils.isNotBlank(contentType) && this.whiteContentType.contains(contentType)) {
                    requestInfo.setRequestBody(request.bodyStr());
                }

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
            final List<Object> newValues = new ArrayList<>();
            for (final Object value : values) {
                // 根据字段类型转换值
                final Object newValue;
                if (JdyFieldTypeEnum.NUMBER.getValue().equals(type)) {
                    newValue = ConvertUtil.toNumber(value);
                } else {
                    // 可以是空字符串, 但是不能是 null
                    newValue = ConvertUtil.toStr(value);
                }
                Assert.notNull(newValue, "字段[" + fieldName + "]值转换失败");
                newValues.add(newValue);
            }
            condition.setValue(newValues);
        }
    }
}
