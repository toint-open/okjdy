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
package cn.toint.okjdy.core.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 简道云 webhook 推送执行动作
 */
@Getter
public enum JdyWebhookOpEnum {
    /**
     * 新增
     */
    DATA_CREATE("data_create"),

    /**
     * 修改
     */
    DATA_UPDATE("data_update"),

    /**
     * 删除
     */
    DATA_REMOVE("data_remove"),

    /**
     * 恢复
     */
    DATA_RECOVER("data_recover"),

    /**
     * 表单修改
     */
    FORM_UPDATE("form_update"),

    /**
     * 测试
     */
    DATA_TEST("data_test");

    private final String value;

    JdyWebhookOpEnum(final String value) {
        this.value = value;
    }

    public static JdyWebhookOpEnum of(final String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        for (JdyWebhookOpEnum item : JdyWebhookOpEnum.values()) {
            if (Objects.equals(item.getValue(), value)) {
                return item;
            }
        }

        return null;
    }
}