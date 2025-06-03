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
package cn.toint.jdy4j.core.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class JdyFieldListResponse {
    /**
     * 字段信息
     * 如果简道云是个空表单, 则对应为空集合, 非null
     */
    @NotNull
    private List<JdyField> widgets;

    /**
     * 系统字段列表（扩展字段、流程字段受功能开关影响，微信增强一旦开启会始终返回）
     */
    @Nullable
    private List<JdySysWidget> sysWidgets;

    /**
     * 表单内数据最新修改时间（可用于判断表单内的数据是否发生变更）
     * 如果当前表单是个新表单, 没有数据, 返回 null
     * 往后只要表单存在过数据, 该字段都会返回值
     */
    @Nullable
    private String dataModifyTime;
}
