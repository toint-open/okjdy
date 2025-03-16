package cn.toint.jdy4j.core.util;

import cn.toint.jdy4j.core.model.JdyWidget;
import cn.toint.jdy4j.core.model.JdyWidgetResponse;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.dromara.hutool.core.lang.Assert;

import java.util.Optional;

/**
 * @author Toint
 * @date 2024/10/19
 */
public class JdyWidgetHolder {
    private static final ThreadLocal<JdyWidgetResponse> THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static JdyWidgetResponse get() {
        return THREAD_LOCAL.get();
    }

    public static JdyWidgetResponse getRequire() {
        final JdyWidgetResponse result = THREAD_LOCAL.get();
        Assert.notNull(result, "当前上下文环境信息不存在");
        return result;
    }

    public static void set(JdyWidgetResponse widgetResponse) {
        THREAD_LOCAL.set(widgetResponse);
    }

    /**
     * 此方法需要用户根据自己程序代码，在适当位置手动触发调用，本SDK里无法判断调用时机
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }

    /**
     * 根据字段名称获取字段类型
     */
    public static String getType(String fieldName) {
        return Optional.ofNullable(JdyWidgetHolder.getRequire())
                .map(JdyWidgetResponse::getWidgetMap)
                .map(map -> map.get(fieldName))
                .map(JdyWidget::getType)
                .orElse(null);
    }

}
