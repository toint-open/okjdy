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

import cn.toint.tool.util.JacksonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author Toint
 * @date 2025/3/17
 */
public class JdyHttpUtil {
    /**
     * 是否为请求超过频率异常
     *
     * @param responseBody responseBody
     * @return 是否为请求超过频率异常
     */
    public static boolean isLimitException(final String responseBody) {
        return Optional.ofNullable(responseBody)
                .filter(StringUtils::isNotBlank)
                .map(JacksonUtil::tryReadTree)
                .map(jsonNode -> jsonNode.path("code"))
                .map(code -> code.asInt(-1))
                .filter(code -> code == 8303 || code == 8304) // 请求超过频率异常
                .isPresent();
    }
}
