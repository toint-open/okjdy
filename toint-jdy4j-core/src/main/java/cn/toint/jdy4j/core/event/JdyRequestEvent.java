package cn.toint.jdy4j.core.event;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.dromara.hutool.core.net.url.UrlBuilder;
import org.dromara.hutool.http.client.Request;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Locale;
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

