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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 过滤方法
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
@AllArgsConstructor
@Getter
public enum JdyMethodEnum {
    /**
     * 不为空
     */
    NOT_EMPTY("not_empty"),
    /**
     * 为空
     */
    EMPTY("empty"),
    /**
     * 等于
     */
    EQ("eq"),
    /**
     * 等于任意一个，最多可传递 200 个
     */
    IN("in"),
    /**
     * 在x与y之间，并且包含x和y本身
     */
    RANGE("range"),
    /**
     * 不等于任意一个，最多可传递 200 个
     */
    NIN("nin"),
    /**
     * 不等于
     */
    NE("ne"),
    /**
     * 包含
     */
    LIKE("like"),
    /**
     * 表示填写了手机号且已验证的值
     */
    VERIFIED("verified"),
    /**
     * 表示填写了手机号但未验证值
     */
    UNVERIFIED("unverified");

    private final String value;
}
