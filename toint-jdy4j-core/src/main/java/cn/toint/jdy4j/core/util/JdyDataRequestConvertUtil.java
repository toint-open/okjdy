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

import cn.toint.jdy4j.core.enums.JdyFieldTypeEnum;
import cn.toint.jdy4j.core.model.*;
import cn.toint.tool.util.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.lang.Assert;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 简道云请求数据转换工具
 * 用于将Java对象转换为简道云API所需的请求格式
 *
 * @author Toint
 * @date 2024/10/20
 */
@Slf4j
public class JdyDataRequestConvertUtil {
    /**
     * 转换器列表
     * k: 字段类型
     * v: 转换器实例
     */
    private static final Map<String, Converter> CONVERTER_MAP = JdyDataRequestConvertUtil.init();

    /**
     * 简道云API要求的值字段名
     */
    private static final String VALUE = "value";

    /**
     * 初始化所有支持的字段类型转换器
     */
    private static Map<String, Converter> init() {
        final Map<String, Converter> converterMap = new HashMap<>();
        // 单行文本转换器
        converterMap.put(JdyFieldTypeEnum.TEXT.getValue(), new TextConverter());
        // 多行文本转换器
        converterMap.put(JdyFieldTypeEnum.TEXT_AREA.getValue(), new TextareaConverter());
        // 数字转换器
        converterMap.put(JdyFieldTypeEnum.NUMBER.getValue(), new NumberConverter());
        // 单选按钮组转换器
        converterMap.put(JdyFieldTypeEnum.RADIO_GROUP.getValue(), new RadioGroupConverter());
        // 日期时间转换器
        converterMap.put(JdyFieldTypeEnum.DATE_TIME.getValue(), new DateTimeConverter());
        // 复选框组转换器
        converterMap.put(JdyFieldTypeEnum.CHECK_BOX_GROUP.getValue(), new CheckBoxGroupConverter());
        // 下拉框转换器
        converterMap.put(JdyFieldTypeEnum.COMBO.getValue(), new ComboConverter());
        // 下拉复选框转换器
        converterMap.put(JdyFieldTypeEnum.COMBO_CHECK.getValue(), new ComboCheckConverter());
        // 地址转换器
        converterMap.put(JdyFieldTypeEnum.ADDRESS.getValue(), new AddressConverter());
        // 定位转换器
        converterMap.put(JdyFieldTypeEnum.LOCATION.getValue(), new LocationConverter());
        // 图片转换器
        converterMap.put(JdyFieldTypeEnum.IMAGE.getValue(), new ImageConverter());
        // 附件转换器
        converterMap.put(JdyFieldTypeEnum.UPLOAD.getValue(), new UploadConverter());
        // 子表单转换器
        converterMap.put(JdyFieldTypeEnum.SUBFORM.getValue(), new SubFormConverter());
        // 成员单选转换器
        converterMap.put(JdyFieldTypeEnum.USER.getValue(), new UserConverter());
        // 成员多选转换器
        converterMap.put(JdyFieldTypeEnum.USERG_ROUP.getValue(), new UserGroupConverter());
        // 部门单选转换器
        converterMap.put(JdyFieldTypeEnum.DEPT.getValue(), new DeptConverter());
        // 部门多选转换器
        converterMap.put(JdyFieldTypeEnum.DEPT_GROUP.getValue(), new DeptGroupConverter());
        // 手机转换器
        converterMap.put(JdyFieldTypeEnum.PHONE.getValue(), new PhoneConverter());
        return converterMap;
    }

    /**
     * 转换请求简道云的 data 字段
     * 将Java对象转换为简道云API所需的请求格式
     *
     * @param data      原始数据对象
     * @param jdyFields 简道云表单字段定义列表
     * @return 转换后的符合简道云API格式的JsonNode
     */
    @Nonnull
    public static JsonNode convert(final @Nonnull JsonNode data, @Nonnull final List<JdyField> jdyFields) {
        Assert.notNull(data, "data must not be null");

        // 如果表单不存在任何字段, 则返回空对象
        if (CollUtil.isEmpty(jdyFields)) {
            return JacksonUtil.ofObjectNode();
        }

        // key: 简道云字段名称, value: 简道云字段对象
        final Map<String, JdyField> fieldNameTypeMap = new HashMap<>();
        jdyFields.forEach(jdyField -> fieldNameTypeMap.put(jdyField.getName(), jdyField));

        // 创建新的数据对象
        final ObjectNode newData = JacksonUtil.ofObjectNode();
        fieldNameTypeMap.forEach((fieldName, field) -> {
            // 获取原始值
            final JsonNode oldValue = data.get(fieldName);
            if (JacksonUtil.isNull(oldValue)) {
                // 对于null值，简道云API会清空该字段
                newData.set(fieldName, JdyDataRequestConvertUtil.ofNewValue(null));
                return;
            }

            // 获取对应的转换器, null 则忽略当前键值对, 避免框架未适配的字段影响正常是用
            final Converter converter = JdyDataRequestConvertUtil.getConverter(field);
            if (converter == null) return;

            // 执行转换, 得到新的 value, 加入到 newValue
            // newValue = {value: xxx}, 如果是 newValue = null, 简道云会保持该字段的当前值, 简道云不会对该字段做任何处理
            try {
                final JsonNode newValue = converter.executeConvert(oldValue, field);
                if (newValue == null) return;
                newData.set(fieldName, newValue);
            } catch (Exception e) {
                log.error("字段[{}]转换失败: {}", fieldName, e.getMessage());
                throw new RuntimeException("字段[" + fieldName + "]转换失败: " + e.getMessage(), e);
            }
        });

        return newData;
    }

    /**
     * 创建简道云API格式的值对象
     * 格式: {"value": xxx}
     *
     * @param value 原始值
     * @return 包装后的值对象
     */
    private static JsonNode ofNewValue(@Nullable final Object value) {
        return JacksonUtil.ofObjectNode().set(VALUE, JacksonUtil.valueToTree(value));
    }

    /**
     * 根据字段定义获取对应的转换器
     *
     * @param jdyField 字段定义
     * @return 对应的转换器，如果不支持该字段类型则返回null
     */
    private static Converter getConverter(final JdyField jdyField) {
        return CONVERTER_MAP.get(jdyField.getType());
    }

    /**
     * 字段值转换器接口
     * 负责将Java对象转换为简道云API所需的格式
     */
    private interface Converter {
        /**
         * 将值转换为简道云API所需的格式
         *
         * @param value    原始值
         * @param jdyField 字段定义
         * @return 转换后的值，格式为{"value": xxx}
         *         如果返回null，简道云API会保持该字段的当前值不变
         */
        @Nullable
        JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField);
    }
    
    /**
     * 只读字段转换器
     * 用于处理那些API不支持修改的字段类型
     */
    private static class ReadOnlyConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            // 返回null表示不修改该字段
            return null;
        }
    }

    /**
     * 文件转换器
     * 用于处理图片和附件等文件类型字段
     */
    private static class FileConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull final JsonNode value, @Nonnull final JdyField jdyField) {
            final JdyFile jdyFile = JacksonUtil.treeToValue(value, JdyFile.class);
            Assert.notNull(jdyFile, "jdyFile convert error, must not be null");

            final Set<String> keys = jdyFile.stream()
                    .map(JdyFile.Detail::getKey)
                    .collect(Collectors.toSet());

            // 如果没有key，则保持字段当前值不变
            // 若需要清空附件字段，请给key字段设置为空字符串或null
            if (keys.isEmpty()) {
                return null;
            } else {
                return JdyDataRequestConvertUtil.ofNewValue(keys);
            }
        }
    }

    /**
     * 字符串转换器
     * 用于处理各种文本类型字段
     */
    private static class StringConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            return JdyDataRequestConvertUtil.ofNewValue(value.asText());
        }
    }

    /**
     * 单行文本转换器
     */
    private static class TextConverter extends StringConverter {
    }

    /**
     * 多行文本转换器
     */
    private static class TextareaConverter extends StringConverter {
    }

    /**
     * 单选按钮组转换器
     */
    private static class RadioGroupConverter extends StringConverter {
    }

    /**
     * 下拉框转换器
     */
    private static class ComboConverter extends StringConverter {
    }

    /**
     * 数字转换器
     */
    private static class NumberConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            if (StringUtils.isBlank(value.asText())) {
                return JdyDataRequestConvertUtil.ofNewValue(null);
            } else {
                try {
                    return JdyDataRequestConvertUtil.ofNewValue(new BigDecimal(value.asText()));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("无效的数字格式: " + value.asText(), e);
                }
            }
        }
    }

    /**
     * 日期时间转换器
     */
    private static class DateTimeConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            try {
                // 处理字符串格式的日期时间
                if (value.isTextual()) {
                    final Instant instant = Instant.parse(value.asText());
                    return JdyDataRequestConvertUtil.ofNewValue(instant.toString());
                }

                // 处理数字格式的日期时间（时间戳）
                if (value.isNumber()) {
                    final long time = value.asLong();
                    final int length = String.valueOf(time).length();
                    
                    // 根据时间戳长度判断单位（秒/毫秒）
                    if (length == 10) {
                        // 秒级时间戳
                        return JdyDataRequestConvertUtil.ofNewValue(Instant.ofEpochSecond(time).toString());
                    } else if (length == 13) {
                        // 毫秒级时间戳
                        return JdyDataRequestConvertUtil.ofNewValue(Instant.ofEpochMilli(time).toString());
                    }
                }
                throw new IllegalArgumentException("不支持的日期时间格式: " + value);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("无效的日期时间格式: " + value.asText(), e);
            }
        }
    }

    /**
     * 字符串数组转换器
     * 用于处理多选类型字段
     */
    private static class StrArrayConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            Assert.isTrue(value.isArray(), "value must be array");

            // 创建字符串数组
            ArrayNode stringArray = JacksonUtil.createArrayNode();

            // 将每个元素转换为字符串
            for (JsonNode element : value) {
                stringArray.add(element.asText());
            }

            return JdyDataRequestConvertUtil.ofNewValue(stringArray);
        }
    }

    /**
     * 复选框组转换器
     */
    private static class CheckBoxGroupConverter extends StrArrayConverter {
    }

    /**
     * 下拉复选框转换器
     */
    private static class ComboCheckConverter extends StrArrayConverter {
    }

    /**
     * 地址转换器
     */
    private static class AddressConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            final JdyAddress address = JacksonUtil.treeToValue(value, JdyAddress.class);
            return JdyDataRequestConvertUtil.ofNewValue(address);
        }
    }

    /**
     * 定位转换器
     */
    private static class LocationConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            final JdyLocation location = JacksonUtil.treeToValue(value, JdyLocation.class);
            return JdyDataRequestConvertUtil.ofNewValue(location);
        }
    }

    /**
     * 图片转换器
     */
    private static class ImageConverter extends FileConverter {
    }

    /**
     * 附件转换器
     */
    private static class UploadConverter extends FileConverter {
    }

    /**
     * 子表单转换器
     */
    private static class SubFormConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            Assert.isTrue(value.isArray(), "value must be array");

            // 获取子表单字段定义, key: 简道云字段名称, value: 简道云字段对象
            final List<JdyField> jdyFields = jdyField.getItems();
            if (CollUtil.isEmpty(jdyFields)) {
                return JdyDataRequestConvertUtil.ofNewValue(JacksonUtil.createArrayNode());
            }

            // 构建子表单字段映射
            final Map<String, JdyField> fieldNameTypeMap = new HashMap<>();
            jdyFields.forEach(jdyFieldItem -> fieldNameTypeMap.put(jdyFieldItem.getName(), jdyFieldItem));

            // 创建子表单数据数组
            final ArrayNode arrayValue = JacksonUtil.createArrayNode();

            // 处理每一行子表单数据
            for (final JsonNode subFormItem : value) {
                final ObjectNode newSubFormItem = JacksonUtil.ofObjectNode();

                // 处理子表单数据ID
                // 注意：根据简道云API，子表单数据ID需要保留并正确处理
                if (subFormItem.has("_id")) {
                    newSubFormItem.set("_id", JdyDataRequestConvertUtil.ofNewValue(subFormItem.get("_id").asText()));
                }

                // 处理子表单中的其他字段
                fieldNameTypeMap.forEach((fieldName, field) -> {
                    // 获取子表单行中该字段的值
                    final JsonNode oldValue = subFormItem.get(fieldName);
                    if (JacksonUtil.isNull(oldValue)) {
                        newSubFormItem.set(fieldName, JdyDataRequestConvertUtil.ofNewValue(null));
                        return;
                    }

                    // 获取该字段类型对应的转换器
                    final Converter converter = JdyDataRequestConvertUtil.getConverter(field);
                    if (converter == null) return;

                    // 执行转换，得到新的value
                    final JsonNode newValue = converter.executeConvert(oldValue, field);
                    if (newValue == null) return;
                    newSubFormItem.set(fieldName, newValue);
                });

                // 将处理好的子表单行添加到数组中
                arrayValue.add(newSubFormItem);
            }

            // 返回包装好的子表单数据
            return JdyDataRequestConvertUtil.ofNewValue(arrayValue);
        }
    }

    /**
     * 成员单选转换器
     */
    private static class UserConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            final JdyUser user = JacksonUtil.treeToValue(value, JdyUser.class);
            return JdyDataRequestConvertUtil.ofNewValue(user.getUsername());
        }
    }

    /**
     * 成员多选转换器
     */
    private static class UserGroupConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            Assert.isTrue(value.isArray(), "value must be array");
            final JdyUserGroup users = JacksonUtil.treeToValue(value, JdyUserGroup.class);
            final Set<String> usernames = users.stream().map(JdyUser::getUsername).collect(Collectors.toSet());
            return JdyDataRequestConvertUtil.ofNewValue(usernames);
        }
    }

    /**
     * 部门单选转换器
     */
    private static class DeptConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            final JdyDept dept = JacksonUtil.treeToValue(value, JdyDept.class);
            return JdyDataRequestConvertUtil.ofNewValue(dept.getDeptNo());
        }
    }

    /**
     * 部门多选转换器
     */
    private static class DeptGroupConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            Assert.isTrue(value.isArray(), "value must be array");
            final JdyDeptGroup depts = JacksonUtil.treeToValue(value, JdyDeptGroup.class);
            final Set<Integer> deptnos = depts.stream().map(JdyDept::getDeptNo).collect(Collectors.toSet());
            return JdyDataRequestConvertUtil.ofNewValue(deptnos);
        }
    }

    /**
     * 手机转换器
     */
    private static class PhoneConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            final JdyPhone phone = JacksonUtil.treeToValue(value, JdyPhone.class);
            return JdyDataRequestConvertUtil.ofNewValue(phone.getPhone());
        }
    }
}