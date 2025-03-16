package cn.toint.jdy4j.core.util;

import cn.toint.jdy4j.core.util.JacksonUtil;
import cn.toint.jdy4j.core.model.JdyWebhookRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.crypto.SecureUtil;
import org.dromara.hutool.http.server.servlet.ServletUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Toint
 * @date 2024/12/31
 */
public class JdyWebhookUtil {

    /**
     * 解码简道云 webhook 请求
     * 为了防止 webhook 的接收服务器被第三方恶意攻击，用户在开发回调接口时，建议对回调请求进行签名校验，以确保回调请求来源来自于简道云。hook会以POST的形式将内容以JSON格式发送给指定地址。
     * 1. 在界面上随机生成一个secret或者自己指定一个secret，并保存。这样，数据推送时就会把加密前的内容和通过secret加密后的内容一起推送到指定地址。
     * 2. 把http请求体字符串作为payload，将其和secret，请求参数里的 nonce、timestamp 按照 “{nonce}:{payload}:{secret}:{timestamp}” 的形式（用冒号分隔），组合为校验字符串 signature。
     * 3. 以 utf-8 编码形式计算 signature 的 sha-1 散列
     * 4. 将 signature 散列的十六进制字符串与 POST 请求 header 中的 ‘X-JDY-Signature’ 做比较，若比较结果相同，则通过签名验证；若比较结果不同，则无法通过检查
     */
    public static JdyWebhookRequest decode(final HttpServletRequest request, final String secret) {
        Assert.notNull(request, "request must not be null");
        Assert.notNull(secret, "secret must not be null");

        final String body = ServletUtil.getBody(request);
        final Map<String, String> param = ServletUtil.getParamMap(request);
        final Map<String, String> header = ServletUtil.getHeaderMap(request);

        final String nonce = param.get("nonce");
        Assert.notBlank(nonce, "nonce must not be blank");
        final String timestamp = param.get("timestamp");
        Assert.notBlank(timestamp, "timestamp must not be blank");
        final String signature = header.get("x-jdy-signature");
        Assert.notBlank(signature, "signature must not be blank");
        final String deliverId = header.get("x-jdy-deliverid");
        Assert.notBlank(deliverId, "deliverId must not be blank");

        final Map<String, String> signatureParamMap = new HashMap<>();
        signatureParamMap.put("nonce", nonce);
        signatureParamMap.put("timestamp", timestamp);
        signatureParamMap.put("secret", secret);
        signatureParamMap.put("payload", body);

        // {nonce}:{payload}:{secret}:{timestamp}
        final String signatureStr = SecureUtil.sha1(StrUtil.formatByMap("{nonce}:{payload}:{secret}:{timestamp}", signatureParamMap));
        Assert.isTrue(Objects.equals(signatureStr, signature), "signature error");

        final JdyWebhookRequest jdyWebhookRequest = JacksonUtil.readValue(body, JdyWebhookRequest.class);
        jdyWebhookRequest.setDeliverId(deliverId);
        return jdyWebhookRequest;
    }
}
