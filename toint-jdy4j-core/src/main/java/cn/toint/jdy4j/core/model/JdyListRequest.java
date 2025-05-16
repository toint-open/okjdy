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

import cn.toint.tool.util.JacksonUtil;
import cn.toint.jdy4j.core.service.JdyAppService;
import cn.toint.jdy4j.core.util.JdyWidgetHolder;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.reflect.ConstructorUtil;
import org.dromara.hutool.extra.spring.SpringUtil;

import java.io.Serializable;
import java.util.*;

/**
 * @author Toint
 * @date 2025/3/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JdyListRequest {
    /**
     * 应用ID
     * 是否必填:是
     */
    @JsonProperty("app_id")
    @NotBlank
    private String appId;
    /**
     * 表单ID
     * 是否必填:是
     */
    @JsonProperty("entry_id")
    @NotBlank
    private String entryId;
    /**
     * 数据ID
     * 是否必填:否
     */
    @JsonProperty("data_id")
    private String dataId;
    /**
     * 需要查询的数据字段
     * 是否必填:否
     */
    @JsonProperty("fields")
    private Collection<String> fields;
    /**
     * 数据筛选器
     * 是否必填:否
     */
    @JsonProperty("filter")
    @Valid
    private JdyFilter filter;
    /**
     * 查询的数据条数
     * 是否必填:否
     */
    @JsonProperty("limit")
    private Integer limit;

    public static JdyListRequest of() {
        return new JdyListRequest();
    }

    public static JdyListRequest of(final String appId, final String entryId) {
        final JdyListRequest listRequest = new JdyListRequest();
        listRequest.setAppId(appId);
        listRequest.setEntryId(entryId);
        return listRequest;
    }

    public static JdyListRequest of(final Object data) {
        final BaseJdyTable jdyTable = JacksonUtil.convertValue(data, BaseJdyTable.class);
        return JdyListRequest.of(jdyTable.getAppId(), jdyTable.getEntryId());
    }

    /**
     * 设置查询字段
     */
    public JdyListRequest select(final Collection<String> fields) {
        this.fields = Set.copyOf(CollUtil.filter(fields, Objects::nonNull));
        return this;
    }

    /**
     * 设置查询字段
     */
    public JdyListRequest select(final String... fields) {
        return this.select(Set.of(ArrayUtil.filter(fields, Objects::nonNull)));
    }

    /**
     * 设置表信息
     */
    public JdyListRequest from(final BaseJdyTable jdyTable) {
        this.appId = jdyTable.getAppId();
        this.entryId = jdyTable.getEntryId();
        return this;
    }

    /**
     * 设置表信息
     */
    public JdyListRequest from(Class<? extends BaseJdyTable> clazz) {
        final BaseJdyTable jdyTable = ConstructorUtil.newInstance(clazz);
        this.appId = jdyTable.getAppId();
        this.entryId = jdyTable.getEntryId();
        return this;
    }

    /**
     * 设置表信息
     */
    public JdyListRequest from(final String appId, final String entryId) {
        this.appId = appId;
        this.entryId = entryId;
        return this;
    }

    /**
     * 为了适配 sql 语法, 在方法中做了一些初始化操作, 包括调用 api 读取表单信息
     */
    public JdyListRequest where() {
        // 查询字段信息
        if (JdyWidgetHolder.get() == null) {
            final JdyWidgetRequest widgetRequest = new JdyWidgetRequest(this.appId, this.entryId);
            final JdyWidgetResponse widgetResponse = SpringUtil.getBean(JdyAppService.class).listWidget(widgetRequest);
            // 缓存表单信息上下文, 在频繁操作表单的时候, 可以避免频繁的通过 api 去读取表单信息
            // 需要注意: 如果上下文未执行 remove, 此时表单信息发生了改变, 框架无法感知
            JdyWidgetHolder.set(widgetResponse);
        }

        if (this.filter == null) {
            this.filter = new JdyFilter();
        }

        if (filter.getCondition() == null) {
            this.filter.setCondition(new HashSet<>());
        }

        return this;
    }

    /**
     * 不为空 isNotNull
     */
    public JdyListRequest notEmpty(final String fieldName) {
        // 初始化操作
        this.where();
        // 新的条件
        final JdyCondition condition = new JdyCondition();
        condition.setField(fieldName);
        condition.setType(JdyWidgetHolder.getType(fieldName));
        condition.setMethod(JdyMethodEnum.NOT_EMPTY.getValue());
        this.filter.getCondition().add(condition);
        return this;
    }

    /**
     * 不为空 isNotNull
     */
    public <T extends Serializable> JdyListRequest notEmpty(final T func) {
        return this.notEmpty(JacksonUtil.getAlias(func));
    }

    /**
     * 为空 isNull
     */
    public JdyListRequest empty(final String fieldName) {
        // 初始化操作
        this.where();
        // 新的条件
        final JdyCondition condition = new JdyCondition();
        condition.setField(fieldName);
        condition.setType(JdyWidgetHolder.getType(fieldName));
        condition.setMethod(JdyMethodEnum.EMPTY.getValue());
        this.filter.getCondition().add(condition);
        return this;
    }

    /**
     * 为空 isNull
     */
    public <T extends Serializable> JdyListRequest empty(final T func) {
        return this.empty(JacksonUtil.getAlias(func));
    }

    /**
     * 等于 ==
     */
    public JdyListRequest eq(final String fieldName, final Object value) {
        // 初始化操作
        this.where();
        // 新的条件
        final JdyCondition condition = new JdyCondition();
        condition.setField(fieldName);
        condition.setType(JdyWidgetHolder.getType(fieldName));
        condition.setMethod(JdyMethodEnum.EQ.getValue());
        condition.setValue(Collections.singletonList(value));
        this.filter.getCondition().add(condition);
        return this;
    }

    /**
     * 等于 ==
     */
    public <T extends Serializable> JdyListRequest eq(final T func, final Object value) {
        return this.eq(JacksonUtil.getAlias(func), value);
    }

    /**
     * 等于任意一个
     */
    public JdyListRequest in(final String fieldName, final Collection<Object> values) {
        // 初始化操作
        this.where();
        // 最大200个条件
        CollUtil.partition(Set.copyOf(CollUtil.filter(values, Objects::nonNull)), 200).forEach(item -> {
            // 新的条件
            final JdyCondition condition = new JdyCondition();
            condition.setField(fieldName);
            condition.setType(JdyWidgetHolder.getType(fieldName));
            condition.setMethod(JdyMethodEnum.IN.getValue());
            condition.setValue(item);
            this.filter.getCondition().add(condition);
        });
        return this;
    }

    /**
     * 等于任意一个
     */
    public JdyListRequest in(final String fieldName, final Object... value) {
        return this.in(fieldName, Set.of(ArrayUtil.filter(value, Objects::nonNull)));
    }

    /**
     * 等于任意一个
     */
    public <T extends Serializable> JdyListRequest in(final T func, final Collection<Object> value) {
        return this.in(JacksonUtil.getAlias(func), value);
    }

    /**
     * 等于任意一个
     */
    public <T extends Serializable> JdyListRequest in(final T func, final Object... value) {
        return this.in(JacksonUtil.getAlias(func), value);
    }

    /**
     * 范围,包含x和y本身
     */
    public JdyListRequest range(final String fieldName, final Object x, final Object y) {
        // 初始化操作
        this.where();
        // 新的条件
        final JdyCondition condition = new JdyCondition();
        condition.setField(fieldName);
        condition.setType(JdyWidgetHolder.getType(fieldName));
        condition.setMethod(JdyMethodEnum.RANGE.getValue());
        condition.setValue(Arrays.asList(x, y)); // asList可以包含null
        this.filter.getCondition().add(condition);
        return this;
    }

    /**
     * 范围,包含x和y本身
     */
    public <T extends Serializable> JdyListRequest range(final T func, final Object x, final Object y) {
        return this.range(JacksonUtil.getAlias(func), x, y);
    }

    /**
     * 大于等于 >=
     */
    public JdyListRequest ge(final String fieldName, final Object value) {
        // 如果value为null,会改变语意
        Assert.notNull(value, "value must not be null");
        return this.range(fieldName, value, null);
    }

    /**
     * 大于等于 >=
     */
    public <T extends Serializable> JdyListRequest ge(final T func, final Object value) {
        // 如果value为null,会改变语意
        Assert.notNull(value, "value must not be null");
        return this.range(func, value, null);
    }

    /**
     * 小于等于 <=
     */
    public JdyListRequest le(final String fieldName, final Object value) {
        // 如果value为null,会改变语意
        Assert.notNull(value, "value must not be null");
        return this.range(fieldName, null, value);
    }

    /**
     * 小于等于 <=
     */
    public <T extends Serializable> JdyListRequest le(final T func, final Object value) {
        // 如果value为null,会改变语意
        Assert.notNull(value, "value must not be null");
        return this.range(func, null, value);
    }

    /**
     * 不等于任意一个
     */
    public JdyListRequest notIn(final String fieldName, final Collection<Object> value) {
        // 初始化操作
        this.where();
        // 最大200个条件
        CollUtil.partition(Set.copyOf(CollUtil.filter(value, Objects::nonNull)), 200).forEach(item -> {
            // 新的条件
            final JdyCondition condition = new JdyCondition();
            condition.setField(fieldName);
            condition.setType(JdyWidgetHolder.getType(fieldName));
            condition.setMethod(JdyMethodEnum.NIN.getValue());
            condition.setValue(item);
            this.filter.getCondition().add(condition);
        });
        return this;
    }

    /**
     * 不等于任意一个
     */
    public JdyListRequest notIn(final String fieldName, final Object... value) {
        return this.notIn(fieldName, Set.of(ArrayUtil.filter(value, Objects::nonNull)));
    }

    /**
     * 不等于任意一个
     */
    public <T extends Serializable> JdyListRequest notIn(final T func, final Collection<Object> value) {
        return this.notIn(JacksonUtil.getAlias(func), value);
    }

    /**
     * 不等于任意一个
     */
    public <T extends Serializable> JdyListRequest notIn(final T func, final Object... value) {
        return this.notIn(JacksonUtil.getAlias(func), value);
    }

    /**
     * 不等于 !=
     */
    public JdyListRequest ne(final String fieldName, final Object value) {
        // 初始化操作
        this.where();
        // 新的条件
        final JdyCondition condition = new JdyCondition();
        condition.setField(fieldName);
        condition.setType(JdyWidgetHolder.getType(fieldName));
        condition.setMethod(JdyMethodEnum.NE.getValue());
        condition.setValue(Collections.singletonList(value));
        this.filter.getCondition().add(condition);
        return this;
    }

    /**
     * 不等于 !=
     */
    public <T extends Serializable> JdyListRequest ne(final T func, final Object value) {
        return this.ne(JacksonUtil.getAlias(func), value);
    }

    /**
     * 包含 like
     */
    public JdyListRequest like(final String fieldName, final Object value) {
        // 初始化操作
        this.where();
        // 新的条件
        final JdyCondition condition = new JdyCondition();
        condition.setField(fieldName);
        condition.setType(JdyWidgetHolder.getType(fieldName));
        condition.setMethod(JdyMethodEnum.LIKE.getValue());
        condition.setValue(Collections.singletonList(value));
        this.filter.getCondition().add(condition);
        return this;
    }

    /**
     * 包含 like
     */
    public <T extends Serializable> JdyListRequest like(final T func, final Object value) {
        return this.like(JacksonUtil.getAlias(func), value);
    }

    /**
     * 表示填写了手机号且已验证的值
     */
    public JdyListRequest verified(final String fieldName, final Object value) {
        // 初始化操作
        this.where();
        // 新的条件
        final JdyCondition condition = new JdyCondition();
        condition.setField(fieldName);
        condition.setType(JdyWidgetHolder.getType(fieldName));
        condition.setMethod(JdyMethodEnum.VERIFIED.getValue());
        condition.setValue(Collections.singletonList(value));
        this.filter.getCondition().add(condition);
        return this;
    }

    /**
     * 表示填写了手机号且已验证的值
     */
    public <T extends Serializable> JdyListRequest verified(final T func, final Object value) {
        return this.verified(JacksonUtil.getAlias(func), value);
    }

    /**
     * 表示填写了手机号但未验证值
     */
    public JdyListRequest unverified(final String fieldName, final Object value) {
        // 初始化操作
        this.where();
        // 新的条件
        final JdyCondition condition = new JdyCondition();
        condition.setField(fieldName);
        condition.setType(JdyWidgetHolder.getType(fieldName));
        condition.setMethod(JdyMethodEnum.UNVERIFIED.getValue());
        condition.setValue(Collections.singletonList(value));
        this.filter.getCondition().add(condition);
        return this;
    }

    /**
     * 表示填写了手机号但未验证值
     */
    public <T extends Serializable> JdyListRequest unverified(final T func, final Object value) {
        return this.unverified(JacksonUtil.getAlias(func), value);
    }

    /**
     * 筛选组合关系
     * 注意:简道云不支持and和or同时使用,所以当前方法只会覆盖原有条件,如果多次调用,只会使用最后一次覆盖的值
     */
    public JdyListRequest and() {
        // 初始化操作
        this.where();

        this.filter.setRelationship(JdyRelationshipEnum.AND.getValue());
        return this;
    }

    /**
     * 筛选组合关系
     * 注意:简道云不支持and和or同时使用,所以当前方法只会覆盖原有条件,如果多次调用,只会使用最后一次覆盖的值
     */
    public JdyListRequest or() {
        // 初始化操作
        this.where();

        this.filter.setRelationship(JdyRelationshipEnum.OR.getValue());
        return this;
    }

    /**
     * 查询数量
     */
    public JdyListRequest limit(final Integer limit) {
        // 初始化操作
        this.where();
        this.limit = limit;
        return this;
    }

}
