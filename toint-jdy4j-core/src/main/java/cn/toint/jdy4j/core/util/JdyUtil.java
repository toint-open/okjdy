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

package cn.toint.jdy4j.core.util;

import cn.toint.tool.json.JacksonUtil;
import cn.toint.jdy4j.core.model.BaseJdyTable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Toint
 * @date 2024/10/27
 */
@Slf4j
public class JdyUtil {
    /**
     * 获取最后一条数据ID
     */
    public static String getLastDataId(final ArrayNode values) {
        return Optional.ofNullable(values)
                .filter(CollUtil::isNotEmpty)
                .map(jsonNodes -> JacksonUtil.treeToValue(values, new TypeReference<List<BaseJdyTable>>() {
                }))
                .map(List::getLast)
                .map(BaseJdyTable::getDataId)
                .orElseThrow();
    }

    /**
     * 校验智能助手响应
     *
     * @param responseStr 简道云响应信息
     */
    public static void validIntelligentAssistantResponse(final String responseStr) {
        Assert.notBlank(responseStr, "简道云智能助手响应校验失败, responseStr must not be blank");
        final JsonNode jsonNode;
        try {
            jsonNode = JacksonUtil.readTree(responseStr);
        } catch (Exception e) {
            final String msg = StrUtil.format("简道云智能助手响应校验失败, 响应信息: {}, cause: {}", responseStr, e.getMessage());
            throw new RuntimeException(msg, e);
        }

        // 校验code
        Optional.ofNullable(jsonNode.get("code"))
                .map(JsonNode::numberValue)
                .filter(number -> Objects.equals(number.intValue(), 0))
                .orElseThrow(() -> new RuntimeException(StrUtil.format("简道云智能助手响应校验 code 失败, 响应信息: {}", responseStr)));
    }

    /**
     * 校验智能助手响应
     */
    public static boolean checkIntelligentAssistantResponse(final String responseStr) {
        try {
            JdyUtil.validIntelligentAssistantResponse(responseStr);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
