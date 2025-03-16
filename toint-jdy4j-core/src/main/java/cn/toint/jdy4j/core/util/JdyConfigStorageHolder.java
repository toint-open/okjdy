package cn.toint.jdy4j.core.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.dromara.hutool.core.lang.Assert;

/**
 * 简道云配置上下文环境
 *
 * @author Toint
 * @date 2024/10/19
 */
public class JdyConfigStorageHolder {
    private static final TransmittableThreadLocal<String> THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static String get() {
        return THREAD_LOCAL.get();
    }

    public static String getRequire() {
        final String result = THREAD_LOCAL.get();
        Assert.notBlank(result, "当前上下文环境信息不存在");
        return result;
    }

    public static void set(String label) {
        THREAD_LOCAL.set(label);
    }

    /**
     * 此方法需要用户根据自己程序代码，在适当位置手动触发调用，本SDK里无法判断调用时机
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
