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

package cn.toint.jdy4j.core.service.impl;

import cn.toint.jdy4j.core.model.*;

import cn.toint.jdy4j.core.service.JdyAppService;
import cn.toint.jdy4j.core.service.JdyDataService;
import cn.toint.jdy4j.core.service.JdyRequestService;
import cn.toint.jdy4j.core.util.JdyConvertUtil;
import cn.toint.jdy4j.core.util.JdyUtil;
import cn.toint.tool.util.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.stream.StreamUtil;
import org.dromara.hutool.extra.validation.ValidationUtil;
import org.dromara.hutool.http.client.Request;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * @author Toint
 * @date 2024/10/19
 */
@Slf4j
public class JdyDataServiceImpl implements JdyDataService {
    /**
     * 简道云请求
     */
    @Resource
    private JdyRequestService jdyRequestService;
    /**
     * 简道云应用
     */
    @Resource
    private JdyAppService jdyAppService;

    @Override
    public ObjectNode selectOne(final JdySelectOneRequest selectOneRequest) {
        // 1.参数校验
        ValidationUtil.validateAndThrowFirst(selectOneRequest);

        // 2.构造请求对象
        final Request request = Request.of(JdyUrlEnum.SELECT_DATA.getUrl())
                .body(JacksonUtil.writeValueAsString(selectOneRequest))
                .method(JdyUrlEnum.SELECT_DATA.getMethod());

        // 3.执行请求
        final JsonNode response = this.jdyRequestService.request(request);

        // 4.读取响应
        return response.get("data").deepCopy();
    }

    @Override
    public <T> T selectOne(final JdySelectOneRequest selectOneRequest, final Class<T> toValueType) {
        final JsonNode jsonNode = this.selectOne(selectOneRequest);
        return JacksonUtil.treeToValue(jsonNode, toValueType);
    }

    @Override
    public <T> T selectOne(final JdySelectOneRequest selectOneRequest, final TypeReference<T> toValueTypeRef) {
        final JsonNode jsonNode = this.selectOne(selectOneRequest);
        return JacksonUtil.treeToValue(jsonNode, toValueTypeRef);
    }

    @Override
    public ArrayNode list(final JdyListRequest listRequest, final Predicate<ArrayNode> predicate) {
        // 参数校验
        ValidationUtil.validateAndThrowFirst(listRequest);

        // 查询的数据条数
        final AtomicInteger limit = new AtomicInteger(listRequest.getLimit() == null || listRequest.getLimit() <= 0 ? Integer.MAX_VALUE : listRequest.getLimit());

        // 查询结果集合
        final ArrayNode results = JacksonUtil.createArrayNode();

        while (true) {
            // 设置查询的数据条数
            final int currentListSize = Math.min(limit.get(), 100); // 本次要查询的数据条数
            listRequest.setLimit(currentListSize); // 本次要查询的数据条数
            limit.addAndGet(-currentListSize); // 扣除余额

            // 构造请求对象, 执行请求
            final Request request = Request.of(JdyUrlEnum.LIST_DATA.getUrl())
                    .body(JacksonUtil.writeValueAsString(listRequest))
                    .method(JdyUrlEnum.LIST_DATA.getMethod());

            final JsonNode response = this.jdyRequestService.request(request);

            // 读取响应, 是个集合
            final JsonNode responseDatas = Optional.ofNullable(response.get("data")).orElseThrow();
            Assert.isTrue(responseDatas.isArray(), "responseDatas data is not array");

            // 加入到结果集合
            try {
                final ArrayNode arrayNode = responseDatas.deepCopy();
                if (predicate == null || predicate.test(arrayNode)) {
                    results.addAll(arrayNode);
                }
            } catch (Exception e) {
                // 回调方法报错则不加入返回集合, 捕获避免影响主线程中断
                log.error(e.getMessage(), e);
            }

            // 几种情况退出查询
            // 1.查询数量已经达到要求
            // 2.已经没有符合要求的数据了, 返回结果为空
            // 3.本次查询的结果数量小于本次要求查询的结果数量
            if (limit.get() <= 0 || CollUtil.isEmpty(responseDatas) || responseDatas.size() < currentListSize) {
                break;
            }

            // 重新赋值最后一条数据的ID,下一次查询会从当前数据的下一条开始返回
            listRequest.setDataId(JdyUtil.getLastDataId(responseDatas.deepCopy()));
        }

        return results;
    }

    @Override
    public ArrayNode list(final JdyListRequest listRequest) {
        return this.list(listRequest, arrayNode -> true);
    }

    @Override
    public <T> List<T> list(JdyListRequest listRequest, Class<T> toValueItemType, final Predicate<ArrayNode> predicate) {
        final ArrayNode arrayNode = this.list(listRequest, predicate);
        return CollUtil.map(arrayNode, nodeItem -> JacksonUtil.treeToValue(nodeItem, toValueItemType));
    }

    @Override
    public <T> List<T> list(final JdyListRequest listRequest, final TypeReference<T> toValueTypeRef, final Predicate<ArrayNode> predicate) {
        final ArrayNode arrayNode = this.list(listRequest, predicate);
        return CollUtil.map(arrayNode, nodeItem -> JacksonUtil.treeToValue(nodeItem, toValueTypeRef));
    }

    @Override
    public <T> List<T> list(JdyListRequest listRequest, Class<T> toValueItemType) {
        return this.list(listRequest, toValueItemType, arrayNode -> true);
    }

    @Override
    public <T> List<T> list(final JdyListRequest listRequest, final TypeReference<T> toValueTypeRef) {
        return this.list(listRequest, toValueTypeRef, arrayNode -> true);
    }

    @Override
    public ObjectNode insertOne(final JdyInsertOneRequest insertOneRequest) {
        // 校验参数
        ValidationUtil.validateAndThrowFirst(insertOneRequest);

        // 转换数据
        final JsonNode data = this.convert(insertOneRequest.getData());
        insertOneRequest.setData(data);

        // 执行请求
        final Request request = Request.of(JdyUrlEnum.INSERT_ONE_DATA.getUrl())
                .body(JacksonUtil.writeValueAsString(insertOneRequest))
                .method(JdyUrlEnum.INSERT_ONE_DATA.getMethod());

        final JsonNode response = this.jdyRequestService.request(request);
        return response.get("data").deepCopy();
    }

    @Override
    public <T> T insertOne(final JdyInsertOneRequest insertOneRequest, final Class<T> valueType) {
        final ObjectNode objectNode = this.insertOne(insertOneRequest);
        return JacksonUtil.treeToValue(objectNode, valueType);
    }

    @Override
    public ObjectNode convert(final JsonNode data, final JdyWidgetResponse widgetResponse) {
        Assert.notEmpty(data, "data must not be empty");

        final Map<String, JdyWidget> widgetMap = widgetResponse.getWidgetMap();
        Assert.notEmpty(widgetMap, "widgetMap must not be empty");

         /*
        2.从普通键值对,转换请求数据格式,要求示例(以下示例可直接用于请求):
        {
          "_widget_1729364621229": {
            "value": "test"
          },
          "_widget_1729364621230": {
            "value": "test"
          },
          "_widget_1729364621231": {
            "value": 1
          },
          "_widget_1729364621232": {
            "value": "2024-10-22T20:26:29Z"
          },
          "_widget_1729599225126": {
            "value": "选项1"
          },
          "_widget_1729599225128": {
            "value": [
              "选项1",
              "选项2",
              "选项3"
            ]
          },
          "_widget_1729599225130": {
            "value": "选项1"
          },
          "_widget_1729599225132": {
            "value": [
              "选项1",
              "选项2",
              "选项3"
            ]
          },
          "_widget_1729599225134": {
            "value": "jdy-0qnp2tgkorok"
          },
          "_widget_1729599225135": {
            "value": [
              "jdy-0qnp2tgkorok",
              "R-gpAdMf3x"
            ]
          },
          "_widget_1729599225136": {
            "value": 843720290
          },
          "_widget_1729599225137": {
            "value": [
              843720290,
              1
            ]
          },
          "_widget_1729599225140": {
            "value": {
              "province": "北京市",
              "city": "北京市",
              "district": "东城区",
              "detail": "123"
            }
          },
          "_widget_1729599225143": {
            "value": {
              "phone": "13888888888",
              "verified": false
            }
          },
          "_widget_1729599225116": {
            "value": [
              {
                "_widget_1729599225144": {
                  "value": "test"
                },
                "_widget_1729599225145": {
                  "value": "test"
                },
                "_widget_1729599225146": {
                  "value": 1
                },
                "_widget_1729599225147": {
                  "value": "2024-10-22T16:00:00Z"
                },
                "_widget_1729599225148": {
                  "value": "选项1"
                },
                "_widget_1729599225150": {
                  "value": [
                    "选项1",
                    "选项2",
                    "选项3"
                  ]
                },
                "_widget_1729599225152": {
                  "value": "选项3"
                },
                "_widget_1729599225154": {
                  "value": [
                    "选项2",
                    "选项1",
                    "选项3"
                  ]
                },
                "_widget_1729599225156": {
                  "value": "R-gpAdMf3x"
                },
                "_widget_1729599225157": {
                  "value": [
                    "R-gpAdMf3x",
                    "jdy-0qnp2tgkorok"
                  ]
                },
                "_widget_1729599225159": {
                  "value": 843720290
                },
                "_widget_1729599225160": {
                  "value": [
                    843720290,
                    1
                  ]
                },
                "_widget_1729599225163": {
                  "value": {
                    "province": "北京市",
                    "city": "北京市",
                    "district": "东城区",
                    "detail": "123"
                  }
                }
              }
            ]
          }
        }
         */
        final ObjectNode jdyRequestBody = JacksonUtil.createObjectNode();

        // 3.执行每一个请求字段的转换
        // 因为外部传入json序列化的时候会默认忽略掉值为null的属性,本框架设置了ignoreNull的逻辑,所以无法匹配的情况也要走逻辑
        // 字段映射键值对
        // k:字段名
        // v:字段对象
        widgetMap.forEach((fieldName, widget) -> {
            // 转换格式
            jdyRequestBody.setAll(JdyConvertUtil.convertJdyRequireFormatPair(fieldName, data.get(fieldName), widget));
        });

        return jdyRequestBody;
    }

    @Override
    public ObjectNode convert(final JsonNode data) {
        Assert.notEmpty(data, "data must not be empty");

        final BaseJdyTable jdyTable = JacksonUtil.treeToValue(data, BaseJdyTable.class);
        Assert.notNull(jdyTable, "jdyTable must not be null");

        // 查询表单业务字段信息
        final JdyWidgetResponse widgetResponse = this.jdyAppService.listWidget(jdyTable);
        Assert.notNull(widgetResponse, "widgetResponse must not be null");

        return this.convert(data, widgetResponse);
    }

    @Override
    public List<String> insertBatch(final JdyInsertBatchRequest insertBatchRequest) {
        // 校验参数
        ValidationUtil.validateAndThrowFirst(insertBatchRequest);

        // 转换数据
        final JsonNode datas = insertBatchRequest.getDatas();
        final BaseJdyTable jdyTable = JacksonUtil.convertValue(datas.get(0), BaseJdyTable.class);
        final JdyWidgetResponse widgetResponse = this.jdyAppService.listWidget(jdyTable); // 查询表单业务字段信息
        final List<ObjectNode> convertedDatas = StreamUtil.of(datas).map(item -> this.convert(item, widgetResponse)).toList();

        // 4.执行请求,单次只可以传入100条数据
        final List<String> responseDataIds = new ArrayList<>(); // 成功添加的数据编号集合
        CollUtil.partition(convertedDatas, 100).forEach(dataGroupItem -> {
            // 请求对象,注意赋值最大100条数据,这里有个问题,如果存在事物id,那么多次新增时,会存在一些问题
            insertBatchRequest.setDatas(JacksonUtil.valueToTree(dataGroupItem));
            final Request request = Request.of(JdyUrlEnum.INSERT_BATCH_DATA.getUrl())
                    .body(JacksonUtil.writeValueAsString(insertBatchRequest))
                    .method(JdyUrlEnum.INSERT_BATCH_DATA.getMethod());

            final JsonNode response = this.jdyRequestService.request(request);
            final List<String> successIds = JacksonUtil.treeToValue(response.get("success_ids"), new TypeReference<List<String>>() {
            });
            // 成功的话,接入到结果集合
            responseDataIds.addAll(successIds);
        });

        return responseDataIds;
    }

    @Override
    public ObjectNode updateOne(final JdyUpdateOneRequest updateOneRequest, final boolean ignoreNull) {
        // 校验参数
        ValidationUtil.validateAndThrowFirst(updateOneRequest);

        // 转换数据
        final ObjectNode convertData = this.convert(updateOneRequest.getData());
        JdyConvertUtil.ignoreNullValue(ignoreNull, convertData);
        updateOneRequest.setData(convertData);

        // 执行请求
        final Request request = Request.of(JdyUrlEnum.UPDATE_ONE_DATA.getUrl())
                .body(JacksonUtil.writeValueAsString(updateOneRequest))
                .method(JdyUrlEnum.UPDATE_ONE_DATA.getMethod());
        final JsonNode response = this.jdyRequestService.request(request);
        return response.get("data").deepCopy();
    }

    @Override
    public <T> T updateOne(final JdyUpdateOneRequest updateOneRequest, final boolean ignoreNull, final Class<T> valueType) {
        final ObjectNode objectNode = this.updateOne(updateOneRequest, ignoreNull);
        return JacksonUtil.treeToValue(objectNode, valueType);
    }

    @Override
    public int updateBatch(final JdyUpdateBatchRequest updateBatchRequest, final boolean ignoreNull) {
        // 校验参数
        ValidationUtil.validateAndThrowFirst(updateBatchRequest);

        // 转换数据
        final ObjectNode convertData = this.convert(updateBatchRequest.getData());
        JdyConvertUtil.ignoreNullValue(ignoreNull, convertData);

        // 执行请求,单次只可以传入100条数据
        final AtomicInteger successSize = new AtomicInteger();
        CollUtil.partition(Set.copyOf(updateBatchRequest.getDataIds()), 100).forEach(dataIdGroupItem -> {
            // 请求对象,注意赋值最大100条数据,这里有个问题
            updateBatchRequest.setDataIds(dataIdGroupItem);
            updateBatchRequest.setData(convertData);
            final Request request = Request.of(JdyUrlEnum.UPDATE_BATCH_DATA.getUrl())
                    .body(JacksonUtil.writeValueAsString(updateBatchRequest))
                    .method(JdyUrlEnum.UPDATE_BATCH_DATA.getMethod());
            final JsonNode response = this.jdyRequestService.request(request);
            final int successCount = response.get("success_count").asInt();
            successSize.addAndGet(successCount);
        });

        return successSize.get();
    }

    @Override
    public boolean deleteOne(final JdyDeleteOneRequest deleteOneRequest) {
        // 1.校验参数
        ValidationUtil.validateAndThrowFirst(deleteOneRequest);

        // 2.执行请求
        final Request request = Request.of(JdyUrlEnum.DELETE_ONE_DATA.getUrl())
                .body(JacksonUtil.writeValueAsString(deleteOneRequest))
                .method(JdyUrlEnum.DELETE_ONE_DATA.getMethod());
        final JsonNode response = this.jdyRequestService.request(request);
        return "status".equals(response.get("status").asText());
    }

    @Override
    public int deleteBatch(JdyDeleteBatchRequest deleteBatchRequest) {
        // 校验参数
        ValidationUtil.validateAndThrowFirst(deleteBatchRequest);

        // 执行请求
        final AtomicInteger successSize = new AtomicInteger();
        CollUtil.partition(deleteBatchRequest.getDataIds(), 100).forEach(dataIdGroupItem -> {
            // 一次最多支持删除 100 条数据。
            deleteBatchRequest.setDataIds(dataIdGroupItem);
            final Request request = Request.of(JdyUrlEnum.DELETE_BATCH_DATA.getUrl())
                    .body(JacksonUtil.writeValueAsString(deleteBatchRequest))
                    .method(JdyUrlEnum.DELETE_BATCH_DATA.getMethod());
            final JsonNode response = this.jdyRequestService.request(request);
            final int successCount = response.get("success_count").asInt();
            successSize.addAndGet(successCount);
        });

        return successSize.get();
    }
}