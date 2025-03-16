/**
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

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * app
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JdyAppRequest {
    /**
     * 单次取数的数据条数，1~100，默认 100。
     */
    @NotNull
    @Min(1)
    @Max(100)
    private Integer limit = 100;
    /**
     * 需要跳过的数据条数，默认 0 。
     */
    @NotNull
    @Min(0)
    private Integer skip = 0;
}
