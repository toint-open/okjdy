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
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.collection.CollUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Toint
 * @date 2025/3/15
 */
@NoArgsConstructor
@Data
public class JdyWidget {
    /**
     * 字段标题
     */
    private String label;
    /**
     * 字段名（设置了字段别名则采用别名，未设置则采用字段ID）
     */
    private String name;
    /**
     * 字段类型；每种字段类型都有对应的数据类型
     */
    private String type;
    /**
     * 子表单
     */
    private List<JdyWidget> items;

    /**
     * 字段映射键值对
     * k:字段名
     * v:字段对象
     *
     * @return 不可变映射
     */
    public static Map<String, JdyWidget> getWidgetMap(final List<JdyWidget> widgets) {
        if (CollUtil.isEmpty(widgets)) {
            return Map.of();
        }

        final Map<String, JdyWidget> widgetMap = new HashMap<>();

        widgets.stream()
                .filter(Objects::nonNull)
                .filter(widget -> StringUtils.isNotBlank(widget.getName()))
                .forEach(widget -> widgetMap.put(widget.getName(), widget));

        return widgetMap;
    }
}
