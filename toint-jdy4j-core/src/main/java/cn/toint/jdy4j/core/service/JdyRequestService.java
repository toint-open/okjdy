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

package cn.toint.jdy4j.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.dromara.hutool.http.client.Request;

/**
 * 简道云请求
 *
 * @author Toint
 * @date 2024/10/19
 */
public interface JdyRequestService {
    /**
     * 请求简道云
     *
     * @param request 请求对象<br>
     *                - 必须含有请求路径, 方法内部会自动替换请求主机<br>
     *                - 必须含有请求方法
     * @throws RuntimeException 请求失败
     */
    JsonNode request(final Request request);
}
