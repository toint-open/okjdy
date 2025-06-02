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
package cn.toint.jdy4j.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dromara.hutool.http.meta.Method;

/**
 * 远端信息枚举
 */
@Getter
@AllArgsConstructor
public enum JdyUrlEnum {
    // 文件
    GET_UPLOAD_TOKEN("获取文件上传凭证和上传地址接口", "https://api.jiandaoyun.com/api/v5/app/entry/file/get_upload_token", Method.POST, 20, 5),

    // 数据
    DELETE_BATCH_DATA("删除多条数据接口", "https://api.jiandaoyun.com/api/v5/app/entry/data/batch_delete", Method.POST, 10, 5),
    DELETE_ONE_DATA("删除单条数据接口", "https://api.jiandaoyun.com/api/v5/app/entry/data/delete", Method.POST, 20, 5),
    UPDATE_BATCH_DATA("修改多条数据接口", "https://api.jiandaoyun.com/api/v5/app/entry/data/batch_update", Method.POST, 10, 5),
    UPDATE_ONE_DATA("修改单条数据接口", "https://api.jiandaoyun.com/api/v5/app/entry/data/update", Method.POST, 20, 5),
    INSERT_BATCH_DATA("新建多条数据接口", "https://api.jiandaoyun.com/api/v5/app/entry/data/batch_create", Method.POST, 10, 5),
    INSERT_ONE_DATA("新建单条数据接口", "https://api.jiandaoyun.com/api/v5/app/entry/data/create", Method.POST, 20, 5),
    LIST_DATA("查询多条数据接口", "https://api.jiandaoyun.com/api/v5/app/entry/data/list", Method.POST, 30, 5),
    SELECT_DATA("查询单条数据接口", "https://api.jiandaoyun.com/api/v5/app/entry/data/get", Method.POST, 30, 5),

    // 应用
    LIST_WIDGET("表单字段查询接口", "https://api.jiandaoyun.com/api/v5/app/entry/widget/list", Method.POST, 30, 5),
    LIST_ENTRY("用户表单查询接口", "https://api.jiandaoyun.com/api/v5/app/entry/list", Method.POST, 30, 5),
    LIST_APP("用户应用查询接口", "https://api.jiandaoyun.com/api/v5/app/list", Method.POST, 30, 5);

    /**
     * 接口名称
     */
    private final String name;
    /**
     * 请求地址
     */
    private final String url;
    /**
     * 请求方法
     */
    private final Method method;
    /**
     * 请求频率
     */
    private final Integer qps;
    /**
     * 版本
     */
    private final Integer version;
}
