/**
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.annotation.AnnotationUtil;
import org.dromara.hutool.core.func.LambdaUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.dromara.hutool.extra.spring.SpringUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author Toint
 * @date 2024/11/15
 */
@Slf4j
public class JacksonUtil {

    private volatile static ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return JacksonUtil.objectMapper;
    }

    public static void setObjectMapper(final ObjectMapper objectMapper) {
        JacksonUtil.objectMapper = objectMapper;
    }

    // ===readValue======================================

    public static <T> T readValue(final String content, final Class<T> valueType) {
        try {
            return JacksonUtil.getObjectMapper().readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T readValue(final String content, final TypeReference<T> valueTypeRef) {
        try {
            return JacksonUtil.getObjectMapper().readValue(content, valueTypeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public static <T> T tryReadValue(final String content, final Class<T> valueType) {
        if (StringUtils.isBlank(content)) {
            return null;
        }

        if (valueType == null) {
            return null;
        }

        try {
            return JacksonUtil.getObjectMapper().readValue(content, valueType);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> T tryReadValue(final String content, final TypeReference<T> valueTypeRef) {
        if (StringUtils.isBlank(content)) {
            return null;
        }

        if (valueTypeRef == null) {
            return null;
        }

        try {
            return JacksonUtil.getObjectMapper().readValue(content, valueTypeRef);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    // ===writeValueAsString=====================================

    public static String writeValueAsString(final Object value) {
        try {
            return JacksonUtil.getObjectMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String tryWriteValueAsString(final Object value) {
        if (value == null) {
            return null;
        }

        try {
            return JacksonUtil.getObjectMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    // ===writeValueAsBytes======================================

    public static byte[] writeValueAsBytes(final Object value) {
        try {
            return JacksonUtil.getObjectMapper().writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static byte[] tryWriteValueAsBytes(final Object value) {
        try {
            return JacksonUtil.getObjectMapper().writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    // ===valueToTree=============================

    /**
     * 转换操作为深拷贝
     */
    public static <T extends JsonNode> T valueToTree(final Object value) {
        return JacksonUtil.getObjectMapper().valueToTree(value);
    }

    // ===convertValue====================================

    /**
     * 转换操作为深拷贝
     */
    public static <T> T convertValue(final Object fromValue, final Class<T> toValueType) {
        return JacksonUtil.getObjectMapper().convertValue(fromValue, toValueType);
    }

    /**
     * 转换操作为深拷贝
     */
    public static <T> T convertValue(final Object fromValue, final TypeReference<T> toValueTypeRef) {
        return JacksonUtil.getObjectMapper().convertValue(fromValue, toValueTypeRef);
    }

    // ===getAlias============================================

    /**
     * 获取属性别名
     *
     * @param func Lambda
     * @return 字段别名
     */
    public static <T extends Serializable> String getAlias(final T func) {
        final JsonProperty annotation = JacksonUtil.getJsonProperty(func);
        return annotation == null ? LambdaUtil.getFieldName(func) : annotation.value();
    }

    /**
     * 获取属性别名
     *
     * @param beanClass beanClass
     * @param fieldName 字段名称
     * @return 字段别名
     */
    public static String getAlias(final Class<?> beanClass, final String fieldName) {
        final JsonProperty annotation = JacksonUtil.getJsonProperty(beanClass, fieldName);

        if (annotation == null) {
            return FieldUtil.getFieldName(FieldUtil.getField(beanClass, fieldName));
        }

        return annotation.value();
    }

    /**
     * 获取@JsonProperty
     *
     * @param func Lambda
     * @return JsonProperty
     */
    public static <T extends Serializable> JsonProperty getJsonProperty(final T func) {
        Assert.notNull(func, "func must not be null");
        return JacksonUtil.getJsonProperty(LambdaUtil.getRealClass(func), LambdaUtil.getFieldName(func));
    }

    /**
     * 获取@JsonProperty
     *
     * @param beanClass beanClass
     * @param fieldName 字段名称
     * @return JsonProperty
     */
    public static JsonProperty getJsonProperty(final Class<?> beanClass, final String fieldName) {
        Assert.notNull(beanClass, "beanClass must not be null");
        Assert.notBlank(fieldName, "fieldName must not be blank");

        final Field field = FieldUtil.getField(beanClass, fieldName);
        if (field == null) {
            return null;
        }

        return AnnotationUtil.getAnnotation(field, JsonProperty.class);
    }

    // ===readTree============================================

    public static JsonNode tryReadTree(final String content) {
        try {
            return JacksonUtil.getObjectMapper().readTree(content);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static JsonNode readTree(final String content) {
        try {
            return JacksonUtil.getObjectMapper().readTree(content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // ===create=======================================

    public static ArrayNode createArrayNode() {
        return JacksonUtil.getObjectMapper().createArrayNode();
    }

    public static ObjectNode createObjectNode() {
        return JacksonUtil.getObjectMapper().createObjectNode();
    }

    // ===treeToValue====================================

    public static <T> T treeToValue(final TreeNode n, final Class<T> valueType) {
        try {
            return JacksonUtil.getObjectMapper().treeToValue(n, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T treeToValue(final TreeNode n, final TypeReference<T> toValueTypeRef) {
        try {
            return JacksonUtil.getObjectMapper().treeToValue(n, toValueTypeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T treeToValue(final TreeNode n, final JavaType valueType) {
        try {
            return JacksonUtil.getObjectMapper().treeToValue(n, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T tryTreeToValue(final TreeNode n, final Class<T> valueType) {
        try {
            return JacksonUtil.getObjectMapper().treeToValue(n, valueType);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> T tryTreeToValue(final TreeNode n, final TypeReference<T> toValueTypeRef) {
        try {
            return JacksonUtil.getObjectMapper().treeToValue(n, toValueTypeRef);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> T tryTreeToValue(final TreeNode n, final JavaType valueType) {
        try {
            return JacksonUtil.getObjectMapper().treeToValue(n, valueType);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    // ===ofObjectNode==========================================
    public static ObjectNode ofObjectNode(final String key, final Object value) {
        return JacksonUtil.getObjectMapper()
                .createObjectNode()
                .set(key, JacksonUtil.valueToTree(value));
    }

    public static ObjectNode ofObjectNode() {
        return JacksonUtil.getObjectMapper().createObjectNode();
    }

    // =============
    public static boolean isNull(final JsonNode value) {
        return value == null || value.isNull();
    }

    public static boolean isEmpty(final JsonNode value) {
        return value == null || value.isNull() || value.isEmpty();
    }
}
