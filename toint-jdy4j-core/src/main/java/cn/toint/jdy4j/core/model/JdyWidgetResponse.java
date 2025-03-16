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

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 示例:{ "name": "_widget_1529400746031", "widgetName": "_widget_1529400746031", "label": "单行文本", "type": "text" }
 */
@Data
@NoArgsConstructor
public class JdyWidgetResponse {
    /**
     * 字段信息
     */
    private List<JdyWidget> widgets;
    /**
     * 系统字段列表（扩展字段、流程字段受功能开关影响，微信增强一旦开启会始终返回）
     */
    private List<JdySysWidget> sysWidgets;
    /**
     * 表单内数据最新修改时间（可用于判断表单内的数据是否发生变更）
     */
    private String dataModifyTime;

    /**
     * 字段映射键值对
     * k:字段名
     * v:字段对象
     *
     * @return 不可变映射
     */
    public Map<String, JdyWidget> getWidgetMap() {
        return JdyWidget.getWidgetMap(this.widgets);
    }
}
