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

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Collection;

/**
 * 过滤条件
 */
@Data
public class JdyCondition {
    /**
     * 字段名
     */
    @JsonProperty("field")
    @NotBlank
    private String field;

    /**
     * 字段类型
     */
    @JsonProperty("type")
    private String type;

    /**
     * 过滤方法：
     * “not_empty”(不为空)，
     * “empty”(为空)，
     * “eq”(等于)，
     * “in”(等于任意一个)，最多可传递 200 个
     * “range”(在x与y之间，并且包含x和y本身)，
     * “nin”(不等于任意一个)，最多可传递 200 个
     * “ne”(不等于),
     * “like”(包含)
     * “verified“(表示填写了手机号且已验证的值)
     * “unverified“(表示填写了手机号但未验证值)
     */
    @JsonProperty("method")
    @NotBlank
    private String method;

    /**
     * 过滤值
     * 集合可能 null, 但元素一定非 null
     */
    @JsonProperty("value")
    private Collection<Object> value;

//    /**
//     * 简道云字段为数字类型字段时, 要求传入的数字, 若传入字符串则条件不生效
//     */
//    private void convertValue() {
//        if (this.type == null || this.value == null) {
//            // ignore
//            return;
//        }
//
//        if ("number".equals(this.type)) {
//            this.value = this.value.stream()
//                    .map(item -> (Object) new BigDecimal(item.toString()))
//                    .toList();
//        } else {
//            this.value = this.value.stream()
//                    .map(item -> (Object) item.toString())
//                    .toList();
//        }
//    }
}
