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

package cn.toint.okjdy.core.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 简道云智能助手工具
 *
 * @author Toint
 * @date 2025/3/17
 */
@Slf4j
public class JdyIntelligentAssistantUtil {
    /**
     * 校验智能助手响应
     *
     * @param responseStr 简道云响应信息
     */
    public static void validIntelligentAssistantResponse(final String responseStr) {
        JdyUtil.validIntelligentAssistantResponse(responseStr);
    }

    /**
     * 校验智能助手响应
     */
    public static boolean checkIntelligentAssistantResponse(final String responseStr) {
        return JdyUtil.checkIntelligentAssistantResponse(responseStr);
    }
}
