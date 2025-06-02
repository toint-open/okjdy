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

import cn.toint.tool.util.JacksonUtil;
import cn.toint.jdy4j.core.enums.JdyFieldTypeEnum;
import cn.toint.jdy4j.core.model.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.collection.iter.IterUtil;
import org.dromara.hutool.core.date.DateTime;
import org.dromara.hutool.core.date.DateUtil;
import org.dromara.hutool.core.map.MapUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 简道云请求数据转换工具
 *
 * @author Toint
 * @date 2024/10/20
 */
@Slf4j
public class JdyConvertUtil {
    /**
     * 转换器列表
     * k:字段类型
     * v:转换器实例
     */
    private static final Map<String, Converter> CONVERTER_MAP = initConverterMap();

    private static final String FIELD_VALUE = "value";

    /**
     * 初始化
     */
    private static Map<String, Converter> initConverterMap() {
        final Map<String, Converter> converterMap = new HashMap<>();
        // 单行文本转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.TEXT.getValue(), new TextConverter());
        // 多行文本转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.TEXT_AREA.getValue(), new TextareaConverter());
        // 数字转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.NUMBER.getValue(), new NumberConverter());
        // 单选按钮组转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.RADIO_GROUP.getValue(), new RadioGroupConverter());
        // 日期时间转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.DATE_TIME.getValue(), new DateTimeConverter());
        // 复选框组转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.CHECK_BOX_GROUP.getValue(), new CheckBoxGroupConverter());
        // 下拉框转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.COMBO.getValue(), new ComboConverter());
        // 下拉复选框转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.COMBO_CHECK.getValue(), new ComboCheckConverter());
        // 地址转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.ADDRESS.getValue(), new AddressConverter());
        // 定位转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.LOCATION.getValue(), new LocaltionConverter());
        // 图片转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.IMAGE.getValue(), new ImageConverter());
        // 附件转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.UPLOAD.getValue(), new UploadConverter());
        // 子表单转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.SUBFORM.getValue(), new SubFormConverter());
        // 成员单选转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.USER.getValue(), new UserConverter());
        // 成员多选转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.USERG_ROUP.getValue(), new UserGroupConverter());
        // 部门单选转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.DEPT.getValue(), new DeptConverter());
        // 部门多选转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.DEPT_GROUP.getValue(), new DeptGroupConverter());
        // 手机转换器
        converterMap.putIfAbsent(JdyFieldTypeEnum.PHONE.getValue(), new PhoneConverter());
        return converterMap;
    }

    /**
     * 根据字段类型获取类型转换器
     *
     * @param widgetType 字段类型
     * @return 类型转换器
     */
    private static Converter getConverter(final String widgetType) {
        return CONVERTER_MAP.get(widgetType);
    }

    /**
     * 根据字段类型获取类型转换器
     *
     * @param widgetDto 字段
     * @return 类型转换器
     */
    private static Converter getConverter(final JdyWidget widgetDto) {
        return CONVERTER_MAP.get(widgetDto.getType());
    }

    /**
     * 将字段键值对转换为简道云要求的请求格式
     *
     * @param key       简道云属性名称
     * @param value     简道云属性值
     * @param widgetDto 简道云字段
     * @return 转换后的键值对, 不允许返回null, 格式要求示例: {"_widget_1432728651402": { "value": "简道云" }}
     */
    public static ObjectNode convertJdyRequireFormatPair(final String key, final JsonNode value, final JdyWidget widgetDto) {
        // 当key为空时,忽略不处理当前字段
        if (StringUtils.isBlank(key)) {
            return JdyConvertUtil.ofIgnore();
        }

        // 当value为空时,简道云会清除当前属性值
        if (value == null || value.isNull()) {
            return JdyConvertUtil.ofClear(key);
        }

        // 获取转换器,如果不存在,则忽略对当前字段的转换
        final Converter converter = JdyConvertUtil.getConverter(widgetDto);
        if (converter == null) {
            return JdyConvertUtil.ofClear(key);
        }

        // 执行值转换
        try {
            return converter.converterJdyRequireFormat(key, value, widgetDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return JdyConvertUtil.ofClear(key);
        }
    }

    /**
     * 创建一个忽略处理的简道云对象
     * 示例:{}
     */
    public static ObjectNode ofIgnore() {
        return JacksonUtil.ofObjectNode();
    }

    /**
     * 创建一个可以清除简道云属性值的对象
     * 示例:{"_widget_1729599225116": {}}
     */
    public static ObjectNode ofClear(final String key) {
        return JacksonUtil.ofObjectNode(key, JacksonUtil.ofObjectNode());
    }

    /**
     * 忽略空值的处理
     *
     * @param ignoreNull 忽略null值,true:null值属性不会请求至简道云,简道云会保持原值处理,false:null至属性会被请求至简道云,简道云会将该属性值清空
     * @param objectNode objectNode
     */
    public static void ignoreNullValue(final boolean ignoreNull, final ObjectNode objectNode) {
        if (!ignoreNull || CollUtil.isEmpty(objectNode)) {
            return;
        }

        // 如果value时空对象,删除该属性,不发送请求至简道云,实现忽略该值的处理
        final Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
        fields.forEachRemaining(field -> {
            if (StringUtils.isBlank(field.getKey()) || IterUtil.isEmpty(field.getValue())) {
                fields.remove();
            }
        });
    }

    /**
     * 将字段键值对转换为简道云要求的请求格式
     * 格式要求示例: {"_widget_1432728651402": { "value": "简道云" }}
     */
    private interface Converter {
        /**
         * 将字段键值对转换为简道云要求的请求格式
         *
         * @param key           简道云属性名称
         * @param value         简道云属性值
         * @param widgetDtoType 简道云字段类型
         * @return 格式要求示例: {"_widget_1432728651402": { "value": "简道云" }}
         * @throws Exception 转换失败
         */
        ObjectNode converterJdyRequireFormat(String key, JsonNode value, JdyWidget widgetDtoType) throws Exception;
    }

    /**
     * 默认转换方法
     *
     * @param key   简道云属性名称
     * @param value 简道云属性值
     * @return 示例:{"_widget_1432728651402": { "value": "简道云" }}
     */
    private static ObjectNode defaultConvertJdyRequireFormat(final String key, final Object value) {
        return JacksonUtil.ofObjectNode(key, JacksonUtil.ofObjectNode(JdyConvertUtil.FIELD_VALUE, value));
    }

    /**
     * 文件转换器
     */
    private static class FileConverter implements Converter {

        @Override
        public ObjectNode converterJdyRequireFormat(final String key, final JsonNode value, final JdyWidget widgetDtoType) {
            // 尝试转换成简道云文件类型, 转换失败则清除操作
            try {
                final JdyFileList jdyFileList = JacksonUtil.convertValue(value, JdyFileList.class);
                final Set<String> fileKeys = jdyFileList.getFileKeys();
                if (CollUtil.isEmpty(fileKeys)) {
                    return JdyConvertUtil.ofClear(key);
                } else {
                    return JdyConvertUtil.defaultConvertJdyRequireFormat(key, JacksonUtil.valueToTree(fileKeys));
                }
            } catch (Exception e) {
                // 清除操作
                log.error(e.getMessage(), e);
                return JdyConvertUtil.ofClear(key);
            }
        }
    }

    /**
     * 字符串转换器
     */
    private static class StringConverter implements Converter {

        @Override
        public ObjectNode converterJdyRequireFormat(String key, JsonNode value, JdyWidget widgetDtoType) {
            // 字符串
            if (value.getNodeType() == JsonNodeType.STRING) {
                final String text = value.asText();
                if (StringUtils.isBlank(text)) {
                    return JdyConvertUtil.ofClear(key);
                } else {
                    return JdyConvertUtil.defaultConvertJdyRequireFormat(key, value);
                }
            }

            // 非字符串, toJson
            return JdyConvertUtil.defaultConvertJdyRequireFormat(key, JacksonUtil.writeValueAsString(value));
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

        @Override
        public ObjectNode converterJdyRequireFormat(String key, JsonNode value, JdyWidget widgetDtoType) {
            try {
                final BigDecimal bigDecimal = new BigDecimal(value.asText());
                return JdyConvertUtil.defaultConvertJdyRequireFormat(key, bigDecimal);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return JdyConvertUtil.ofClear(key);
            }
        }
    }

    /**
     * 日期时间转换器
     */
    private static class DateTimeConverter implements Converter {

        @Override
        public ObjectNode converterJdyRequireFormat(String key, JsonNode value, JdyWidget widgetDtoType) {
            try {
                final DateTime dateTime = DateUtil.parse(value.asText());
                return JdyConvertUtil.defaultConvertJdyRequireFormat(key, dateTime.toInstant().toString());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return JdyConvertUtil.ofClear(key);
            }
        }
    }

    /**
     * 复选框组转换器
     */
    private static class StrArrayConverter implements Converter {

        @Override
        public ObjectNode converterJdyRequireFormat(String key, JsonNode value, JdyWidget widgetDtoType) {
            if (!value.isArray()) {
                return JdyConvertUtil.ofClear(key);
            } else {
                return JdyConvertUtil.defaultConvertJdyRequireFormat(key, value);
            }
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
        @Override
        public ObjectNode converterJdyRequireFormat(String key, JsonNode value, JdyWidget widgetDtoType) {
            try {
                final JdyAddress address = JacksonUtil.treeToValue(value, JdyAddress.class);
                return JdyConvertUtil.defaultConvertJdyRequireFormat(key, address);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return JdyConvertUtil.ofClear(key);
            }
        }
    }

    /**
     * 定位转换器
     */
    private static class LocaltionConverter implements Converter {
        @Override
        public ObjectNode converterJdyRequireFormat(String key, JsonNode value, JdyWidget widgetDtoType) {
            try {
                final JdyLocation location = JacksonUtil.treeToValue(value, JdyLocation.class);
                return JdyConvertUtil.defaultConvertJdyRequireFormat(key, location);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return JdyConvertUtil.ofClear(key);
            }
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
        @Override
        public ObjectNode converterJdyRequireFormat(String key, JsonNode value, JdyWidget widgetDtoType) {
            // 字段映射键值对
            final Map<String, JdyWidget> widgetMap = JdyWidget.getWidgetMap(widgetDtoType.getItems());
            if (MapUtil.isEmpty(widgetMap)) {
                return JdyConvertUtil.ofClear(key);
            }

            // 不是集合也不是数组, 执行清除
            if (!value.isArray() || value.isEmpty()) {
                return JdyConvertUtil.ofClear(key);
            }

            /*
            返回对象,示例:
            [
              {
                "_id": {
                  "value": "606290aba392ca00076da0a9"
                },
                "_widget_1615777739744": {
                  "value": "王五"
                }
              },
              {
                "_id": {
                  "value": "706290aba392ca00076da0a9"
                },
                "_widget_2615777739744": {
                  "value": "张三"
                }
              }
            ]
            */

            /*
            将每一条子表单数据转换成ObjectNode,示例:
            [
              {
                "_id": "606290aba392ca00076da0a9",
                "_widget_1615777739744": "王五"
              },
              {
                "_id": "606290aba392ca00076da0a9",
                "_widget_1615777739744": "王五"
              }
            ]
             */

            /*
            每一条记录都得转换格式,示例:
            [
              {
                "_id": "606290aba392ca00076da0a9",
                "_widget_1615777739744": "王五"
              },
              {
                "_id": "606290aba392ca00076da0a9",
                "_widget_1615777739744": "王五"
              }
            ]

            变成下面这样️

            [
              {
                "_widget_2615777739744": {
                  "value": "张三"
                }
              },
              {
                "_widget_2615777739744": {
                  "value": "张三"
                }
              }
            ]
             */

            // 返回对象
            final ArrayNode responses = JacksonUtil.createArrayNode();

            // 原始对象 -> json
            value.forEach(valueItem -> { // 这里的valueItem是集合中一个元素
                // valueItem -> responseItem
                // 要将objectNodeItem转换,responseItem是转换后请求数据格式的对象
                final ObjectNode responseItem = JacksonUtil.createObjectNode();

                // 转换对象中所有字段
                valueItem.fields().forEachRemaining(fieldEntry -> {
                    final String entryKey = fieldEntry.getKey();
                    final JsonNode entryValue = fieldEntry.getValue();

                    // 当前请求字段无效,因为简道云声明的表单内没有当前字段
                    if (!widgetMap.containsKey(entryKey)) {
                        return;
                    }

                    // 将每个键值对都送进去转换格式,返回的示例:{"_widget_2615777739744":{"value":"张三"}}
                    final ObjectNode pair = JdyConvertUtil.convertJdyRequireFormatPair(entryKey, entryValue, widgetMap.get(entryKey));
                    responseItem.setAll(pair);
                });

                // 结果装到集合
                responses.add(responseItem);
            });

            return JdyConvertUtil.defaultConvertJdyRequireFormat(key, responses);
        }
    }

    /**
     * 成员单选转换器
     */
    private static class UserConverter implements Converter {
        @Override
        public ObjectNode converterJdyRequireFormat(String key, JsonNode value, JdyWidget widgetDtoType) {
            try {
                final JdyUser user = JacksonUtil.treeToValue(value, JdyUser.class);
                final String username = user.getUsername();
                if (StringUtils.isEmpty(username)) {
                    return JdyConvertUtil.ofClear(key);
                } else {
                    return JdyConvertUtil.defaultConvertJdyRequireFormat(key, username);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return JdyConvertUtil.ofClear(key);
            }
        }
    }

    /**
     * 成员多选转换器
     */
    private static class UserGroupConverter implements Converter {
        @Override
        public ObjectNode converterJdyRequireFormat(String key, JsonNode value, JdyWidget widgetDtoType) {
            try {
                final JdyUserGroupList users = JacksonUtil.treeToValue(value, JdyUserGroupList.class);
                final Set<String> userNames = users.getUsernames();
                if (CollUtil.isEmpty(userNames)) {
                    return JdyConvertUtil.ofClear(key);
                } else {
                    return JdyConvertUtil.defaultConvertJdyRequireFormat(key, userNames);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return JdyConvertUtil.ofClear(key);
            }
        }
    }

    /**
     * 部门单选转换器
     */
    private static class DeptConverter implements Converter {
        @Override
        public ObjectNode converterJdyRequireFormat(String key, JsonNode value, JdyWidget widgetDtoType) {
            try {
                final JdyDept dept = JacksonUtil.treeToValue(value, JdyDept.class);
                final Integer deptNo = dept.getDeptNo();
                if (deptNo == null) {
                    return JdyConvertUtil.ofClear(key);
                } else {
                    return JdyConvertUtil.defaultConvertJdyRequireFormat(key, deptNo);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return JdyConvertUtil.ofClear(key);
            }
        }
    }

    /**
     * 部门多选转换器
     */
    private static class DeptGroupConverter implements Converter {
        @Override
        public ObjectNode converterJdyRequireFormat(String key, JsonNode value, JdyWidget widgetDtoType) {
            try {
                final JdyDeptGroupList depts = JacksonUtil.treeToValue(value, JdyDeptGroupList.class);
                final Set<Integer> deptNos = depts.getDeptNos();
                if (CollUtil.isEmpty(deptNos)) {
                    return JdyConvertUtil.ofClear(key);
                } else {
                    return JdyConvertUtil.defaultConvertJdyRequireFormat(key, deptNos);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return JdyConvertUtil.ofClear(key);
            }
        }
    }

    /**
     * 手机转换器
     */
    private static class PhoneConverter implements Converter {
        @Override
        public ObjectNode converterJdyRequireFormat(String key, JsonNode value, JdyWidget widgetDtoType) {
            try {
                final JdyPhone phone = JacksonUtil.treeToValue(value, JdyPhone.class);
                final String phoneNum = phone.getPhone();
                if (StringUtils.isEmpty(phoneNum)) {
                    return JdyConvertUtil.ofClear(key);
                } else {
                    return JdyConvertUtil.defaultConvertJdyRequireFormat(key, phoneNum);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return JdyConvertUtil.ofClear(key);
            }
        }
    }
}