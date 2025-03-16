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

import cn.toint.jdy4j.core.model.JdyWidget;
import cn.toint.jdy4j.core.model.JdyWidgetResponse;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.dromara.hutool.core.lang.Assert;

import java.util.Optional;

/**
 * 简道云表单字段上下文
 * <p>在频繁操作表单的时候, 可以避免频繁的通过 api 去读取表单信息</p>
 * <p>需要注意: 如果上下文未执行 remove, 此时表单信息发生了改变, 框架无法感知</p>
 *
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
