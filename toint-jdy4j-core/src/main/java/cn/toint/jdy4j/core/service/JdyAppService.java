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
    List<JdyApp> listAllApp();

    /**
     * 用户应用查询接口
     */
    List<JdyApp> listApp(JdyAppListRequest appRequest);

    /**
     * 用户全量表单查询接口
     */
    List<JdyEntry> listAllEntry(String appId);

    /**
     * 用户表单查询接口
     */
    List<JdyEntry> listEntry(JdyEntryListRequest entryRequest);

    /**
     * 表单字段查询接口
     */
    JdyFieldListResponse listWidget(JdyFieldListRequest widgetRequest);

    /**
     * 表单字段查询接口
     */
    JdyFieldListResponse listWidget(JdyDo jdyTable);
}
