package cn.toint.jdy4j.core.util;

import cn.toint.tool.json.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.dromara.hutool.http.client.ClientConfig;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.http.client.engine.ClientEngine;
import org.dromara.hutool.http.client.engine.ClientEngineFactory;
import org.dromara.hutool.http.client.engine.okhttp.OkHttpEngine;

import java.util.Optional;

/**
 * @author Toint
 * @date 2025/3/17
 */
public class JdyHttpUtil {
    /**
     * 简道云默认 http 客户端
     * 服务端3秒内未响应第一次内容则超时报错
     */
    private static final ClientEngine CLIENT_ENGINE = ClientEngineFactory.createEngine(OkHttpEngine.class.getName())
            .init(ClientConfig.of().setTimeout(3000));

    public static ClientEngine getClientEngine() {
        return JdyHttpUtil.CLIENT_ENGINE;
    }

    public static Response request(final Request request) {
        return JdyHttpUtil.CLIENT_ENGINE.send(request);
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
}
