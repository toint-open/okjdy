package cn.toint.jdy4j.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.dromara.hutool.http.client.Request;

/**
 * 简道云请求
 *
 * @author Toint
 * @date 2024/10/19
 */
public interface JdyRequestService {
    /**
     * 请求简道云
     *
     * @param request 请求对象<br>
     *                - 必须含有请求路径, 方法内部会自动替换请求主机<br>
     *                - 必须含有请求方法
     * @throws RuntimeException 请求失败
     */
    JsonNode request(final Request request);
}
