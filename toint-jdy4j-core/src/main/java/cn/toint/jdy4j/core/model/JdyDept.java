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
import lombok.Data;

/**
 * 部门
 */
@Data
public class JdyDept {
    /**
     * name
     */
    @JsonProperty("name")
    private String name;
    /**
     * deptNo
     */
    @JsonProperty("dept_no")
    private Integer deptNo;
    /**
     * type
     */
    @JsonProperty("type")
    private Integer type;
    /**
     * parentNo
     */
    @JsonProperty("parent_no")
    private Integer parentNo;
    /**
     * status
     */
    @JsonProperty("status")
    private Integer status;
    /**
     * integrateId
     */
    @JsonProperty("integrate_id")
    private Integer integrateId;
}
