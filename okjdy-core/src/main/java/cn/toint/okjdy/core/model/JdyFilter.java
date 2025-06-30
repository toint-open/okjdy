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

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;
import java.util.HashSet;

/**
 * 数据筛选器
 */
@Data
public class JdyFilter {
    /**
     * 筛选组合关系；“and”(满足所有过滤条件), “or”(满足任一过滤条件)
     */
    @JsonProperty("rel")
    @NotBlank
    private String relationship = JdyRelationshipEnum.AND.getValue();
    /**
     * 筛选组合关系；“and”(满足所有过滤条件), “or”(满足任一过滤条件)
     */
    @JsonProperty("cond")
    @Valid
    @NotNull
    private Collection<JdyCondition> condition = new HashSet<>();
}
