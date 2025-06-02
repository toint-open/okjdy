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
package cn.toint.jdy4j.core.model;

import cn.toint.tool.util.Assert;
import cn.toint.tool.util.JacksonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.func.SerFunction;
import org.dromara.hutool.core.reflect.ConstructorUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Toint
 * @date 2025/3/15
 */
@Data
public class JdyListDataRequest {
    /**
     * 应用ID
     */
    @JsonProperty("app_id")
    @NotBlank
    private String appId;

    /**
     * 表单ID
     */
    @JsonProperty("entry_id")
    @NotBlank
    private String entryId;

    /**
     * 数据ID
     */
    @JsonProperty("data_id")
    private String dataId;

    /**
     * 需要查询的数据字段
     */
    @JsonProperty("fields")
    private Collection<String> fields;

    /**
     * 数据筛选器
     */
    @JsonProperty("filter")
    @Valid
    @NotNull
    private JdyFilter filter = new JdyFilter();

    /**
     * 查询的数据条数
     */
    @JsonProperty("limit")
    private int limit = 100;

    public JdyListDataRequest() {
    }

    @Nonnull
    public static JdyListDataRequest of() {
        return new JdyListDataRequest();
    }

    /**
     * 设置查询字段
     */
    @Nonnull
    public JdyListDataRequest select(@Nonnull final String... fields) {
        if (this.fields == null) {
            this.fields = new HashSet<>();
        }

        if (ArrayUtil.isNotEmpty(fields)) {
            this.fields.addAll(Arrays.stream(fields)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet()));
        }

        return this;
    }

    /**
     * 设置表信息
     */
    @Nonnull
    public JdyListDataRequest from(@Nonnull final JdyDo jdyTable) {
        this.from(jdyTable.getAppId(), jdyTable.getEntryId());
        return this;
    }

    /**
     * 设置表信息
     */
    @Nonnull
    public JdyListDataRequest from(@Nonnull final Class<? extends JdyDo> clazz) {
        final JdyDo jdyTable = ConstructorUtil.newInstance(clazz);
        this.from(jdyTable);
        return this;
    }

    /**
     * 设置表信息
     */
    @Nonnull
    public JdyListDataRequest from(@Nonnull final String appId, @Nonnull final String entryId) {
        Assert.notBlank(appId, "appId must not be blank");
        Assert.notBlank(entryId, "entryId must not be blank");
        this.appId = appId;
        this.entryId = entryId;
        return this;
    }

    /**
     * 不为空
     */
    @Nonnull
    public JdyListDataRequest notEmpty(@Nonnull final String fieldName) {
        Assert.notBlank(fieldName, "fieldName must not be blank");
        final JdyCondition condition = new JdyCondition();
        condition.setField(fieldName);
        condition.setMethod(JdyMethodEnum.NOT_EMPTY.getValue());
        this.addCondition(condition);
        return this;
    }

    /**
     * 不为空
     */
    @Nonnull
    public <T, R> JdyListDataRequest notEmpty(@Nonnull final SerFunction<T, R> func) {
        return this.notEmpty(JacksonUtil.getAlias(func));
    }

    /**
     * 为空
     */
    @Nonnull
    public JdyListDataRequest empty(@Nonnull final String fieldName) {
        Assert.notBlank(fieldName, "fieldName must not be blank");
        final JdyCondition condition = new JdyCondition();
        condition.setField(fieldName);
        condition.setMethod(JdyMethodEnum.EMPTY.getValue());
        this.addCondition(condition);
        return this;
    }

    /**
     * 为空
     */
    @Nonnull
    public <T, R> JdyListDataRequest empty(@Nonnull final SerFunction<T, R> func) {
        return this.empty(JacksonUtil.getAlias(func));
    }

    /**
     * 等于, value 为空忽略本条件
     */
    @Nonnull
    public JdyListDataRequest eq(@Nonnull final String fieldName, @Nullable final Object value) {
        Assert.notBlank(fieldName, "fieldName must not be blank");

        if (value != null) {
            final JdyCondition condition = new JdyCondition();
            condition.setField(fieldName);
            condition.setMethod(JdyMethodEnum.EQ.getValue());
            condition.setValue(List.of(value));
            this.addCondition(condition);
        }

        return this;
    }

    /**
     * 等于, value 为空忽略本条件
     */
    @Nonnull
    public <T, R> JdyListDataRequest eq(@Nonnull final SerFunction<T, R> func, @Nullable final Object value) {
        return this.eq(JacksonUtil.getAlias(func), value);
    }

    /**
     * 等于任意一个, value 为空忽略本条件
     */
    @Nonnull
    public JdyListDataRequest in(@Nonnull final String fieldName, @Nullable final Collection<Object> values) {
        Assert.notBlank(fieldName, "fieldName must not be blank");
        if (CollUtil.isNotEmpty(values)) {
            final Set<Object> set = values.stream().filter(Objects::nonNull).collect(Collectors.toSet());
            Assert.isTrue(set.size() <= 200, "条件参数最多可传递200个");
            final JdyCondition condition = new JdyCondition();
            condition.setField(fieldName);
            condition.setValue(set);
            this.addCondition(condition);
        }
        return this;
    }

    /**
     * 等于任意一个, value 为空忽略本条件
     */
    @Nonnull
    public <T, R> JdyListDataRequest in(@Nonnull final SerFunction<T, R> func, @Nullable final Collection<Object> value) {
        return this.in(JacksonUtil.getAlias(func), value);
    }

    /**
     * 范围,包含x和y本身, xy 都为 null 忽略本条件
     */
    @Nonnull
    public JdyListDataRequest range(@Nonnull final String fieldName, @Nullable final Object x, @Nullable final Object y) {
        Assert.notBlank(fieldName, "fieldName must not be blank");
        if (x != null || y != null) {
            final JdyCondition condition = new JdyCondition();
            condition.setField(fieldName);
            condition.setMethod(JdyMethodEnum.RANGE.getValue());
            condition.setValue(Arrays.asList(x, y));
            this.addCondition(condition);
        }
        return this;
    }

    /**
     * 范围,包含x和y本身, xy 都为 null 忽略本条件
     */
    @Nonnull
    public <T, R> JdyListDataRequest range(@Nonnull final SerFunction<T, R> func, @Nullable final Object x, @Nullable final Object y) {
        return this.range(JacksonUtil.getAlias(func), x, y);
    }

    /**
     * 大于等于 >=, value 为 null 忽略本条件
     */
    @Nonnull
    public JdyListDataRequest ge(@Nonnull final String fieldName, @Nullable final Object value) {
        Assert.notBlank(fieldName, "fieldName must not be blank");
        if (value != null) {
            this.range(fieldName, value, null);
        }
        return this;
    }

    /**
     * 大于等于 >=, value 为 null 忽略本条件
     */
    @Nonnull
    public <T, R> JdyListDataRequest ge(@Nonnull final SerFunction<T, R> func, @Nullable final Object value) {
        if (value != null) {
            this.range(func, value, null);
        }
        return this;
    }

    /**
     * 小于等于 <=, value 为 null 忽略本条件
     */
    @Nonnull
    public JdyListDataRequest le(@Nonnull final String fieldName, @Nullable final Object value) {
        Assert.notBlank(fieldName, "fieldName must not be blank");
        if (value != null) {
            this.range(fieldName, null, value);
        }
        return this;
    }

    /**
     * 小于等于 <=, value 为 null 忽略本条件
     */
    @Nonnull
    public <T, R> JdyListDataRequest le(@Nonnull final SerFunction<T, R> func, @Nullable final Object value) {
        if (value != null) {
            return this.range(func, null, value);
        }
        return this;
    }

    /**
     * 不等于任意一个, value 为 null 忽略本条件
     */
    @Nonnull
    public JdyListDataRequest notIn(@Nonnull final String fieldName, @Nullable final Collection<Object> value) {
        Assert.notBlank(fieldName, "fieldName must not be blank");
        if (CollUtil.isNotEmpty(value)) {
            final Set<Object> set = value.stream().filter(Objects::nonNull).collect(Collectors.toSet());
            Assert.isTrue(set.size() <= 200, "条件参数最多可传递200个");
            final JdyCondition condition = new JdyCondition();
            condition.setField(fieldName);
            condition.setMethod(JdyMethodEnum.NIN.getValue());
            condition.setValue(set);
            this.addCondition(condition);
        }
        return this;
    }

    /**
     * 不等于任意一个, value 为 null 忽略本条件
     */
    @Nonnull
    public <T, R> JdyListDataRequest notIn(@Nonnull final SerFunction<T, R> func, @Nullable final Collection<Object> value) {
        return this.notIn(JacksonUtil.getAlias(func), value);
    }

    /**
     * 不等于 !=, value 为 null 忽略本条件
     */
    @Nonnull
    public JdyListDataRequest ne(@Nonnull final String fieldName, @Nullable final Object value) {
        Assert.notBlank(fieldName, "fieldName must not be blank");
        if (value != null) {
            final JdyCondition condition = new JdyCondition();
            condition.setField(fieldName);
            condition.setMethod(JdyMethodEnum.NE.getValue());
            condition.setValue(List.of(value));
            this.addCondition(condition);
        }
        return this;
    }

    /**
     * 不等于 !=, value 为 null 忽略本条件
     */
    @Nonnull
    public <T, R> JdyListDataRequest ne(@Nonnull final SerFunction<T, R> func, @Nullable final Object value) {
        return this.ne(JacksonUtil.getAlias(func), value);
    }

    /**
     * 包含 like, value 为 null 忽略本条件
     */
    @Nonnull
    public JdyListDataRequest like(@Nonnull final String fieldName, @Nullable final Object value) {
        Assert.notBlank(fieldName, "fieldName must not be blank");
        if (value != null) {
            final JdyCondition condition = new JdyCondition();
            condition.setField(fieldName);
            condition.setMethod(JdyMethodEnum.LIKE.getValue());
            condition.setValue(List.of(value));
            this.addCondition(condition);
        }
        return this;
    }

    /**
     * 包含 like
     */
    @Nonnull
    public <T, R> JdyListDataRequest like(@Nonnull final SerFunction<T, R> func, @Nullable final Object value) {
        return this.like(JacksonUtil.getAlias(func), value);
    }

    /**
     * 表示填写了手机号且已验证的值
     */
    @Nonnull
    public JdyListDataRequest verified(@Nonnull final String fieldName) {
        Assert.notBlank(fieldName, "fieldName must not be blank");
        final JdyCondition condition = new JdyCondition();
        condition.setField(fieldName);
        condition.setMethod(JdyMethodEnum.VERIFIED.getValue());
        this.addCondition(condition);
        return this;
    }

    /**
     * 表示填写了手机号且已验证的值
     */
    @Nonnull
    public <T, R> JdyListDataRequest verified(@Nonnull final SerFunction<T, R> func) {
        return this.verified(JacksonUtil.getAlias(func));
    }

    /**
     * 表示填写了手机号但未验证值
     */
    @Nonnull
    public JdyListDataRequest unverified(@Nonnull final String fieldName) {
        Assert.notBlank(fieldName, "fieldName must not be blank");
        final JdyCondition condition = new JdyCondition();
        condition.setField(fieldName);
        condition.setMethod(JdyMethodEnum.UNVERIFIED.getValue());
        this.addCondition(condition);
        return this;
    }

    /**
     * 表示填写了手机号但未验证值
     */
    @Nonnull
    public <T, R> JdyListDataRequest unverified(@Nonnull final SerFunction<T, R> func) {
        return this.unverified(JacksonUtil.getAlias(func));
    }

    /**
     * 筛选组合关系
     * 注意:简道云不支持and和or同时使用,所以当前方法只会覆盖原有条件,如果多次调用,只会使用最后一次覆盖的值
     */
    @Nonnull
    public JdyListDataRequest and() {
        this.filter.setRelationship(JdyRelationshipEnum.AND.getValue());
        return this;
    }

    /**
     * 筛选组合关系
     * 注意:简道云不支持and和or同时使用,所以当前方法只会覆盖原有条件,如果多次调用,只会使用最后一次覆盖的值
     */
    @Nonnull
    public JdyListDataRequest or() {
        this.filter.setRelationship(JdyRelationshipEnum.OR.getValue());
        return this;
    }

    /**
     * 查询数量
     */
    @Nonnull
    public JdyListDataRequest limit(final int limit) {
        this.limit = limit;
        return this;
    }

    private void addCondition(final JdyCondition jdyCondition) {
        this.filter.getCondition().add(jdyCondition);
    }
}
