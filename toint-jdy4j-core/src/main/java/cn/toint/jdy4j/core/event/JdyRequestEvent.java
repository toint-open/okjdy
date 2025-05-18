package cn.toint.jdy4j.core.event;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.dromara.hutool.http.client.Request;
import org.springframework.context.ApplicationEvent;

/**
 * @author Toint
 * @date 2025/5/18
 */
public class JdyRequestEvent extends ApplicationEvent {

    public JdyRequestEvent(final RequestInfo source) {
        super(source);
    }

    public JdyRequestEvent(final Request request, final String response, final boolean status) {
        super(new RequestInfo(request, response, status));
    }

    @Override
    public RequestInfo getSource() {
        return (RequestInfo) super.getSource();
    }

    @Data
    @AllArgsConstructor
    public static class RequestInfo {
        private final Request request;
        private final String  response;
        private final boolean status;
    }
}

