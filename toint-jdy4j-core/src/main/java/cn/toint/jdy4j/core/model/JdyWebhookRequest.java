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

import cn.toint.jdy4j.core.enums.JdyWebhookOpEnum;
import cn.toint.tool.util.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * @author Toint
 * @date 2024/12/31
 */
@Data
public class JdyWebhookRequest {
    /**
     * 推送请求编号, 可以通过该字段完成请求的去重, 防止重复接收同一个事件
     */
    private String deliverId;

    /**
     * 事件名称
     */
    private String op;

    /**
     * 事件时间戳, 精确到毫秒
     */
    private Long opTime;

    /**
     * 推送数据, 可能是表单数据, 也可能是表单结构
     */
    private JsonNode data;

    public JdyWebhookOpEnum opEnum() {
        return JdyWebhookOpEnum.of(this.op);
    }

    public <T> T data(final Class<T> valueType) {
        if (data == null) {
            return null;
        }

        return JacksonUtil.treeToValue(this.data, valueType);
    }

    public <T> T data(final TypeReference<T> toValueTypeRef) {
        if (data == null) {
            return null;
        }

        return JacksonUtil.treeToValue(this.data, toValueTypeRef);
    }
}
