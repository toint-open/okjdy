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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * 成员
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public
class JdyUser {
    /**
     * 成员昵称
     */
    @JsonProperty("name")
    private String name;
    /**
     * 成员账号
     * 成员信息中username表示通讯录的成员编号（企业内唯一），如果是企业集成模式下同步的用户，相当于是钉钉或者企业微信的 user_id
     * 不同企业之间可能存在重复
     */
    @JsonProperty("username")
    private String username;
    /**
     * 成员状态
     * -1:离职
     * 0:未加入
     * 1:已加入
     */
    @JsonProperty("status")
    private Integer status;
    /**
     * 成员类型
     * 0:常规成员
     * 2:企业互联外部对接人
     */
    @JsonProperty("type")
    private Integer type;
    /**
     * 成员所在部门编号列表
     */
    @JsonProperty("departments")
    private Collection<Integer> departments;
    /**
     * 集成模式同步成员关联 ID
     * 仅在集成模式下返回，且在企业互联接口(外部对接人)不返回
     */
    @JsonProperty("integrate_id")
    private String integrateId;
}
