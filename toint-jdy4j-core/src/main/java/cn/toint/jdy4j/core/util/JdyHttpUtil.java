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

import cn.toint.tool.util.HttpClientUtil;
import cn.toint.tool.util.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.dromara.hutool.http.client.ClientConfig;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.http.client.engine.ClientEngine;
import org.dromara.hutool.http.client.engine.okhttp.OkHttpEngine;

import java.util.Optional;

/**
 * @author Toint
 * @date 2025/3/17
 */
public class JdyHttpUtil {
    /**
     * 简道云默认 http 客户端
     * 服务端10秒内未响应第一次内容则超时报错
     */
    private static final ClientEngine CLIENT_ENGINE = HttpClientUtil.clientEngine(OkHttpEngine.class, ClientConfig.of().setTimeout(10000));

    public static ClientEngine getClientEngine() {
        return JdyHttpUtil.CLIENT_ENGINE;
    }

    public static Response request(final Request request) {
        return JdyHttpUtil.CLIENT_ENGINE.send(request);
    }

    /**
     * 是否为请求超过频率异常
     *
     * @param status 响应状态码
     * @param responseBody responseBody
     * @return 是否为请求超过频率异常
     */
    public static boolean isLimitException(final int status, final String responseBody) {
        if (status >= 200 && status < 300) {
            return false;
        }

        return Optional.ofNullable(responseBody)
                .map(JacksonUtil::readTree)
                .map(jsonNode -> jsonNode.get("code"))
                .map(JsonNode::numberValue)
                .map(Number::intValue)
                .filter(code -> code == 8303 || code == 8304) // 请求超过频率异常
                .isPresent();
    }
}
