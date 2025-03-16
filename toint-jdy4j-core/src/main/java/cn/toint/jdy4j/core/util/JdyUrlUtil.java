package cn.toint.jdy4j.core.util;

import cn.toint.jdy4j.core.constant.JdyConstant;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.net.url.UrlBuilder;

/**
 * @author Toint
 * @date 2025/3/3
 */
public class JdyUrlUtil {
    /**
     * 将简道云请求地址转换为企业请求地址
     *
     * @param originalUrl 原始地址
     * @param serverUrl   服务地址
     * @return 替换后的新对象
     */
    public static UrlBuilder toCorpUrl(final String originalUrl, final String serverUrl) {
        final UrlBuilder serverUrlBuilder;
        if (StringUtils.isBlank(serverUrl)) {
            serverUrlBuilder = UrlBuilder.of(JdyConstant.DEFAULT_SERVER_URL);
        } else {
            serverUrlBuilder = UrlBuilder.of(serverUrl);
        }

        return UrlBuilder.of(originalUrl)
                .setScheme(serverUrlBuilder.getScheme())
                .setHost(serverUrlBuilder.getHost());
    }
}
