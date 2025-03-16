package cn.toint.jdy4j.core.service;

import cn.toint.jdy4j.core.model.*;


import java.util.List;

/**
 * 简道云应用
 *
 * @author Toint
 * @date 2024/10/19
 */
public interface JdyAppService {
    /**
     * 用户全量应用查询接口
     */
    List<JdyAppResponse> listAllApp();

    /**
     * 用户应用查询接口
     */
    List<JdyAppResponse> listApp(JdyAppRequest appRequest);

    /**
     * 用户全量表单查询接口
     */
    List<JdyEntryResponse> listAllEntry(String appId);

    /**
     * 用户表单查询接口
     */
    List<JdyEntryResponse> listEntry(JdyEntryRequest entryRequest);

    /**
     * 表单字段查询接口
     */
    JdyWidgetResponse listWidget(JdyWidgetRequest widgetRequest);

    /**
     * 表单字段查询接口
     */
    JdyWidgetResponse listWidget(BaseJdyTable jdyTable);
}
