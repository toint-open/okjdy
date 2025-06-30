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
package cn.toint.okjdy.core.model;

import lombok.Data;

import java.util.List;

/**
 * @author Toint
 * @date 2025/3/15
 */
@Data
public class JdyField {
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
    private List<JdyField> items;
}
