package cn.toint.jdy4j.core.service.impl;

import cn.toint.tool.json.JacksonUtil;
import cn.toint.jdy4j.core.model.*;

import cn.toint.jdy4j.core.service.JdyAppService;
import cn.toint.jdy4j.core.service.JdyRequestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.extra.validation.ValidationUtil;
import org.dromara.hutool.http.client.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Toint
 * @date 2024/10/19
 */
public class JdyAppServiceImpl implements JdyAppService {
    /**
     * 简道云请求
     */
    @Resource
    private JdyRequestService jdyRequestService;

    @Override
    public List<JdyAppResponse> listAllApp() {
        final List<JdyAppResponse> responses = new ArrayList<>();
        final JdyAppRequest appRequest = new JdyAppRequest(100, 0);

        while (true) {
            final List<JdyAppResponse> onceResponse = this.listApp(appRequest);

            // 直至查询结果为空集合
            if (CollUtil.isEmpty(onceResponse)) {
                break;
            }

            responses.addAll(onceResponse);
            appRequest.setSkip(responses.size());
        }

        return responses;
    }

    @Override
    public List<JdyAppResponse> listApp(final JdyAppRequest appRequest) {
        ValidationUtil.validateAndThrowFirst(appRequest);

        final Request request = Request.of(JdyUrlEnum.LIST_APP.getUrl())
                .body(JacksonUtil.writeValueAsString(appRequest))
                .method(JdyUrlEnum.LIST_APP.getMethod());

        final JsonNode response = this.jdyRequestService.request(request);
        return JacksonUtil.treeToValue(response.get("apps"), new TypeReference<>() {
        });
    }

    @Override
    public List<JdyEntryResponse> listAllEntry(final String appId) {
        Assert.notBlank(appId);

        final List<JdyEntryResponse> responses = new ArrayList<>();
        final JdyEntryRequest entryRequest = new JdyEntryRequest(appId, 100, 0);

        while (true) {
            final List<JdyEntryResponse> onceResponses = this.listEntry(entryRequest);
            // 直至查询结果为空集合
            if (CollUtil.isEmpty(onceResponses)) {
                break;
            }
            responses.addAll(onceResponses);
            entryRequest.setSkip(responses.size());
        }

        return responses;
    }

    @Override
    public List<JdyEntryResponse> listEntry(final JdyEntryRequest entryRequest) {
        ValidationUtil.validateAndThrowFirst(entryRequest);

        final Request request = Request.of(JdyUrlEnum.LIST_ENTRY.getUrl())
                .body(JacksonUtil.writeValueAsString(entryRequest))
                .method(JdyUrlEnum.LIST_ENTRY.getMethod());

        final JsonNode response = this.jdyRequestService.request(request);
        return JacksonUtil.treeToValue(response.get("forms"), new TypeReference<>() {
        });
    }

    @Override
    public JdyWidgetResponse listWidget(final JdyWidgetRequest widgetRequest) {
        ValidationUtil.validateAndThrowFirst(widgetRequest);

        final Request request = Request.of(JdyUrlEnum.LIST_WIDGET.getUrl())
                .body( JacksonUtil.writeValueAsString(widgetRequest))
                .method(JdyUrlEnum.LIST_WIDGET.getMethod());

        final JsonNode response = this.jdyRequestService.request(request);
        return JacksonUtil.treeToValue(response, JdyWidgetResponse.class);
    }

    @Override
    public JdyWidgetResponse listWidget(final BaseJdyTable jdyTable) {
        return this.listWidget(new JdyWidgetRequest(jdyTable.getAppId(), jdyTable.getEntryId()));
    }
}
