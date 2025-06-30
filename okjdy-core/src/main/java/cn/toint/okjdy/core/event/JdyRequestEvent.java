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

package cn.toint.okjdy.core.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * @author Toint
 * @date 2025/5/18
 */
public class JdyRequestEvent extends ApplicationEvent {

    public JdyRequestEvent(final RequestInfo source) {
        super(source);
    }

    @Override
    public RequestInfo getSource() {
        return (RequestInfo) super.getSource();
    }

    @Data
    public static class RequestInfo {
        /**
         * 请求地址
         */
        private String url;

        /**
         * 请求方法
         */
        private String method;

        /**
         * 请求体
         */
        private String requestBody;

        /**
         * 请求头
         */
        private Map<String, ? extends Collection<String>> requestHeader;

        /**
         * 响应体
         */
        private String responseBody;

        /**
         * 响应头
         */
        private Map<String, ? extends Collection<String>> responseHeader;

        /**
         * 响应状态码
         */
        private Integer status;

        /**
         * 请求时间
         */
        private LocalDateTime requestTime;

        /**
         * 响应时间
         */
        private LocalDateTime responseTime;

        /**
         * 耗时
         */
        private Long durationTime;
    }
}

