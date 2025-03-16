package cn.toint.jdy4j.core.util;

import cn.toint.jdy4j.core.util.JacksonUtil;
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
     * 是否为请求超过频率异常
     *
     * @param responseBody responseBody
     * @return 是否为请求超过频率异常
     */
    public static boolean isLimitException(final String responseBody) {
        return Optional.ofNullable(responseBody)
                .map(JacksonUtil::readTree)
                .map(jsonNode -> jsonNode.get("code"))
                .map(JsonNode::numberValue)
                .map(Number::intValue)
                .filter(code -> code == 8303 || code == 8304) // 请求超过频率异常
                .isPresent();
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
            checkIntelligentAssistantResponse(responseStr);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
