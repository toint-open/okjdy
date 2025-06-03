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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 简道云请求数据转换工具
 *
 * @author Toint
 * @date 2024/10/20
 */
@Slf4j
public class JdyDataRequestConvertUtil {
    /**
     * 转换器列表
     * k:字段类型
     * v:转换器实例
     */
    private static final Map<String, Converter> CONVERTER_MAP = JdyDataRequestConvertUtil.init();

    private static final String VALUE = "value";

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
     *
     * @param data      data
     * @param jdyFields 简道云字段, 如果不存在任何字段, 则返回无任何属性的 JsonNode = {}
     * @return 转换后的 data
     */
    @Nonnull
    public static JsonNode convert(final @Nonnull JsonNode data, @Nonnull final List<JdyField> jdyFields) {
        Assert.notNull(data, "data must not be null");

        // 如果表单不存在任何字段, 则返回无任何属性的 JsonNode = {}
        if (CollUtil.isEmpty(jdyFields)) {
            return JacksonUtil.ofObjectNode();
        }

        // key: 简道云字段名称, value: 简道云字段对象
        final Map<String, JdyField> fieldNameTypeMap = new HashMap<>();
        jdyFields.forEach(jdyField -> fieldNameTypeMap.put(jdyField.getName(), jdyField));

        // 新数据对象, 用于返回
        final ObjectNode newData = JacksonUtil.ofObjectNode();
        fieldNameTypeMap.forEach((fieldName, field) -> {
            // 旧的值, 可能为 null, 此处不会忽略 null, 会严格按照传入的数据一比一传输给简道云
            // 也就是说, 如果是 null, 简道云会对应的清空该字段的值, 格式: {"字段名": {"value": null}}
            final JsonNode oldValue = data.get(fieldName);
            if (JacksonUtil.isNull(oldValue)) {
                newData.set(fieldName, JdyDataRequestConvertUtil.ofNewValue(null));
                return;
            }

            // 转换器, 如果为空则忽略当前键值对, 避免框架未适配的字段影响正常是用
            final Converter converter = JdyDataRequestConvertUtil.getConverter(field);
            if (converter == null) return;

            // 执行转换, 得到新的 value, 加入到 newValue
            // newValue = {value: xxx}, 如果是 newValue = null, 简道云会保持该字段的当前值, 简道云不会对该字段做任何处理
            final JsonNode newValue = converter.executeConvert(oldValue, field);
            newData.set(fieldName, newValue);
        });

        return newData;
    }

    /**
     * 简道云新 value
     *
     * @return {"value": null}
     */
    private static JsonNode ofNewValue(@Nullable final Object value) {
        return JacksonUtil.ofObjectNode().set(VALUE, JacksonUtil.valueToTree(value));
    }

    /**
     * 根据字段类型获取类型转换器
     *
     * @param jdyField 字段
     * @return 类型转换器
     */
    private static Converter getConverter(final JdyField jdyField) {
        return CONVERTER_MAP.get(jdyField.getType());
    }

    /**
     * 将字段键值对转换为简道云要求的请求格式
     * 格式要求示例: {"_widget_1432728651402": { "value": "简道云" }}
     */
    private interface Converter {
        /**
         * 将 value 转换为简道云要求的请求格式
         *
         * @param value    简道云字段值
         * @param jdyField 简道云字段类型
         * @return 格式要求示例: {"value": "xxx"}, 返回 null, 简道云会保持该字段的当前值, 简道云不会对该字段做任何处理
         */
        @Nullable
        JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField);
    }

    /**
     * 文件转换器
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

            // 如果没有 key, 则说明当前字段应该保持原样, 返回 null, 否则会清空该字段
            // 若需要清空附件字段, 请给 key 字段设置为空字符串或 null, 简道云会清空该字段
            if (keys.isEmpty()) {
                return null;
            } else {
                return JacksonUtil.ofObjectNode().set(VALUE, JacksonUtil.valueToTree(keys));
            }

        }
    }

    /**
     * 字符串转换器
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
    private static class TextConverter extends StringConverter implements Converter {

    }

    /**
     * 多行文本转换器
     */
    private static class TextareaConverter extends StringConverter implements Converter {

    }

    /**
     * 单选按钮组转换器
     */
    private static class RadioGroupConverter extends StringConverter implements Converter {

    }

    /**
     * 下拉框转换器
     */
    private static class ComboConverter extends StringConverter implements Converter {

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
                return JdyDataRequestConvertUtil.ofNewValue(new BigDecimal(value.asText()));
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
            // 请使用 String 或 Instant 映射简道云的日期字段, 其他类型暂未适配, 不保证可用
            return JdyDataRequestConvertUtil.ofNewValue(value.asText());
        }
    }

    /**
     * 复选框组转换器
     */
    private static class StrArrayConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            Assert.isTrue(value.isArray(), "value must be array");

            // 创建一个 JSON 数组来存储转换后的字符串值
            ArrayNode stringArray = JacksonUtil.createArrayNode();

            // 遍历 JSON 集合中的每个元素
            for (JsonNode element : value) {
                // 将每个元素转换为字符串并添加到数组中
                stringArray.add(element.asText());
            }

            return JdyDataRequestConvertUtil.ofNewValue(stringArray);
        }
    }

    /**
     * 复选框组转换器
     */
    private static class CheckBoxGroupConverter extends StrArrayConverter implements Converter {

    }

    /**
     * 下拉复选框转换器
     */
    private static class ComboCheckConverter extends StrArrayConverter implements Converter {

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
    private static class ImageConverter extends FileConverter implements Converter {

    }

    /**
     * 附件转换器
     */
    private static class UploadConverter extends FileConverter implements Converter {
    }


    /**
     * 子表单转换器
     */
    private static class SubFormConverter implements Converter {
        @Nullable
        @Override
        public JsonNode executeConvert(@Nonnull JsonNode value, @Nonnull JdyField jdyField) {
            Assert.isTrue(value.isArray(), "value must be array");

            // 如果表单不存在任何字段, 则返回空数组
            final List<JdyField> jdyFields = jdyField.getItems();
            if (CollUtil.isEmpty(jdyFields)) {
                return JdyDataRequestConvertUtil.ofNewValue(JacksonUtil.createArrayNode());
            }

            // key: 简道云字段名称, value: 简道云字段对象
            final Map<String, JdyField> fieldNameTypeMap = new HashMap<>();
            jdyFields.forEach(jdyFieldItem -> fieldNameTypeMap.put(jdyFieldItem.getName(), jdyFieldItem));

            // 创建子表单数据数组
            final ArrayNode arrayValue = JacksonUtil.createArrayNode();

            // 遍历子表单中的每一行数据
            for (final JsonNode subFormItem : value) {
                // 创建一个新的子表单行数据对象
                final ObjectNode newSubFormItem = JacksonUtil.ofObjectNode();

                // 处理子表单数据ID (_id字段)
                // 如果存在_id字段，则需要保留该ID，并按照简道云API要求的格式进行处理
                if (subFormItem.has("_id")) {
                    // 注释: 子表单数据ID需要包装在{"value": "xxx"}格式中
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