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

package cn.toint.jdy4j.core.service;

import cn.toint.jdy4j.core.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.function.Predicate;

/**
 * 简道云数据
 *
 * @author Toint
 * @date 2024/10/19
 */
public interface JdyDataService {
    /**
     * 获取一条数据
     */
    ObjectNode selectOne(JdyDataGetRequest selectOneRequest);

    /**
     * 获取一条数据
     */
    <T> T selectOne(JdyDataGetRequest selectOneRequest, Class<T> toValueType);

    /**
     * 获取一条数据
     */
    <T> T selectOne(JdyDataGetRequest selectOneRequest, final TypeReference<T> toValueTypeRef);

    /**
     * 查询数据列表
     * 该接口的返回数据，始终按照数据 ID 正序排列。
     * 若要设置循环调取数据，可以利用 data_id 字段来设置参数避免调取重复数据。
     * 如需要查询 230 条数据：
     * 第一次查询时可以不传 data_id 字段，若设置 limit 为 100 ，则第一次返回了前 100 条数据；
     * 第二次，用第 100 条数据的 data_id 进行查询，若设置 limit 为100，则第二次返回 101～200 这 100 条数据；
     * 第三次，用第 200 条数据的 data_id 进行查询，若设置 limit 为100，则第三次返回 201～230 这 30 条数据。
     * 由于第三次返回结果只有 30 条，未达到设置的 limit 上限100，则说明查询结束。
     *
     * @param listRequest listRequest
     * @param predicate   predicate, 入参: 请求参数, 返回: boolean, true: addAll, false: 忽略结果
     * @return 数据列表
     */
    ArrayNode list(JdyListDataRequest listRequest, Predicate<ArrayNode> predicate);

    /**
     * 查询数据列表
     * 该接口的返回数据，始终按照数据 ID 正序排列。
     * 若要设置循环调取数据，可以利用 data_id 字段来设置参数避免调取重复数据。
     * 如需要查询 230 条数据：
     * 第一次查询时可以不传 data_id 字段，若设置 limit 为 100 ，则第一次返回了前 100 条数据；
     * 第二次，用第 100 条数据的 data_id 进行查询，若设置 limit 为100，则第二次返回 101～200 这 100 条数据；
     * 第三次，用第 200 条数据的 data_id 进行查询，若设置 limit 为100，则第三次返回 201～230 这 30 条数据。
     * 由于第三次返回结果只有 30 条，未达到设置的 limit 上限100，则说明查询结束。
     *
     * @param listRequest listRequest
     * @return 数据列表
     */
    ArrayNode list(JdyListDataRequest listRequest);

    /**
     * 查询数据列表
     * 该接口的返回数据，始终按照数据 ID 正序排列。
     * 若要设置循环调取数据，可以利用 data_id 字段来设置参数避免调取重复数据。
     * 如需要查询 230 条数据：
     * 第一次查询时可以不传 data_id 字段，若设置 limit 为 100 ，则第一次返回了前 100 条数据；
     * 第二次，用第 100 条数据的 data_id 进行查询，若设置 limit 为100，则第二次返回 101～200 这 100 条数据；
     * 第三次，用第 200 条数据的 data_id 进行查询，若设置 limit 为100，则第三次返回 201～230 这 30 条数据。
     * 由于第三次返回结果只有 30 条，未达到设置的 limit 上限100，则说明查询结束。
     *
     * @param listRequest listRequest
     * @param predicate   predicate, 入参: 请求参数, 返回: boolean, true: addAll, false: 忽略结果
     * @return 数据列表
     */
    <T> List<T> list(JdyListDataRequest listRequest, Class<T> toValueItemType, Predicate<ArrayNode> predicate);

    /**
     * 查询数据列表
     * 该接口的返回数据，始终按照数据 ID 正序排列。
     * 若要设置循环调取数据，可以利用 data_id 字段来设置参数避免调取重复数据。
     * 如需要查询 230 条数据：
     * 第一次查询时可以不传 data_id 字段，若设置 limit 为 100 ，则第一次返回了前 100 条数据；
     * 第二次，用第 100 条数据的 data_id 进行查询，若设置 limit 为100，则第二次返回 101～200 这 100 条数据；
     * 第三次，用第 200 条数据的 data_id 进行查询，若设置 limit 为100，则第三次返回 201～230 这 30 条数据。
     * 由于第三次返回结果只有 30 条，未达到设置的 limit 上限100，则说明查询结束。
     *
     * @param listRequest listRequest
     * @param predicate   predicate, 入参: 请求参数, 返回: boolean, true: addAll, false: 忽略结果
     * @return 数据列表
     */
    <T> List<T> list(JdyListDataRequest listRequest, TypeReference<T> toValueTypeRef, Predicate<ArrayNode> predicate);

    /**
     * 查询数据列表
     * 该接口的返回数据，始终按照数据 ID 正序排列。
     * 若要设置循环调取数据，可以利用 data_id 字段来设置参数避免调取重复数据。
     * 如需要查询 230 条数据：
     * 第一次查询时可以不传 data_id 字段，若设置 limit 为 100 ，则第一次返回了前 100 条数据；
     * 第二次，用第 100 条数据的 data_id 进行查询，若设置 limit 为100，则第二次返回 101～200 这 100 条数据；
     * 第三次，用第 200 条数据的 data_id 进行查询，若设置 limit 为100，则第三次返回 201～230 这 30 条数据。
     * 由于第三次返回结果只有 30 条，未达到设置的 limit 上限100，则说明查询结束。
     *
     * @param listRequest listRequest
     * @return 数据列表
     */
    <T> List<T> list(JdyListDataRequest listRequest, Class<T> toValueItemType);

    /**
     * 查询数据列表
     * 该接口的返回数据，始终按照数据 ID 正序排列。
     * 若要设置循环调取数据，可以利用 data_id 字段来设置参数避免调取重复数据。
     * 如需要查询 230 条数据：
     * 第一次查询时可以不传 data_id 字段，若设置 limit 为 100 ，则第一次返回了前 100 条数据；
     * 第二次，用第 100 条数据的 data_id 进行查询，若设置 limit 为100，则第二次返回 101～200 这 100 条数据；
     * 第三次，用第 200 条数据的 data_id 进行查询，若设置 limit 为100，则第三次返回 201～230 这 30 条数据。
     * 由于第三次返回结果只有 30 条，未达到设置的 limit 上限100，则说明查询结束。
     *
     * @param listRequest listRequest
     * @return 数据列表
     */
    <T> List<T> list(JdyListDataRequest listRequest, TypeReference<T> toValueTypeRef);

    /**
     * 新增一条数据
     * 注：使用 API 添加数据时，会触发的事件有新数据提交提醒、聚合表计算&校验、数据操作日志、数据量统计。也可以通过请求参数来控制是否发起流程。但是不会触发重复值校验、必填校验。
     * 另外，系统字段 和以下所列举的字段不支持添加和修改数据：
     * 1.分割线
     * 2.手写签名
     * 3.选择数据、查询
     * 4.流水号（提交后系统生成）
     *
     * @param insertOneRequest insertOneRequest
     * @return 返回提交后的完整数据，内容同查询单条数据接口
     */
    ObjectNode insertOne(JdyDataSaveRequest insertOneRequest);

    /**
     * 新增一条数据
     * 注：使用 API 添加数据时，会触发的事件有新数据提交提醒、聚合表计算&校验、数据操作日志、数据量统计。也可以通过请求参数来控制是否发起流程。但是不会触发重复值校验、必填校验。
     * 另外，系统字段 和以下所列举的字段不支持添加和修改数据：
     * 1.分割线
     * 2.手写签名
     * 3.选择数据、查询
     * 4.流水号（提交后系统生成）
     *
     * @param insertOneRequest insertOneRequest
     * @param valueType        反序列化
     * @return 返回提交后的完整数据，内容同查询单条数据接口
     */
    <T> T insertOne(JdyDataSaveRequest insertOneRequest, Class<T> valueType);

    /**
     * 转换请求数据格式
     * 注意事项:
     * 1.请求体中不存在当前字段键,简道云不会处理该字段.
     * 2.请求体中存在当前字段键,但值为null时,简道云不会处理该字段.示例:"_widget_1697608837845": null
     * 3.请求体存在当前字段键,但其值为空对象时,简道云将该值赋值为空.示例:"_widget_1697608837845": {}
     * 4.请求体存在当前字段键,但其对象属性值为null时,简道云将该值赋值为空.示例:"_widget_1697608837845": {value:null}
     * 5.请求体存在当前字段键,但其值中键名为value的值不为null时,简道云会将其赋予实际值.
     * 但是要注意,想要清除字段值,请使用3.中的格式,因为4.中的格式可以将当行文本赋值为空字符,但是赋值数字字段,会默认为0,会造成歧义.
     *
     * @param data 转换前数据,输入的键值对中,当value为空值,简道云会清除当前属性值
     * @return 转换后数据
     */
    ObjectNode convert(JsonNode data, JdyFieldListResponse widgetResponse);

    /**
     * 转换请求数据格式
     * 注意事项:
     * 1.请求体中不存在当前字段键,简道云不会处理该字段.
     * 2.请求体中存在当前字段键,但值为null时,简道云不会处理该字段.示例:"_widget_1697608837845": null
     * 3.请求体存在当前字段键,但其值为空对象时,简道云将该值赋值为空.示例:"_widget_1697608837845": {}
     * 4.请求体存在当前字段键,但其对象属性值为null时,简道云将该值赋值为空.示例:"_widget_1697608837845": {value:null}
     * 5.请求体存在当前字段键,但其值中键名为value的值不为null时,简道云会将其赋予实际值.
     * 但是要注意,想要清除字段值,请使用3.中的格式,因为4.中的格式可以将当行文本赋值为空字符,但是赋值数字字段,会默认为0,会造成歧义.
     *
     * @param data 转换前数据,输入的键值对中,当value为空值,简道云会清除当前属性值
     * @return 转换后数据
     */
    ObjectNode convert(JsonNode data);

    /**
     * 新建多条数据
     * 注：使用 API 添加数据时，会触发的事件有新数据提交提醒、聚合表计算&校验、数据操作日志、数据量统计。也可以通过请求参数来控制是否发起流程。但是不会触发重复值校验、必填校验。
     * 另外，系统字段 和以下所列举的字段不支持添加和修改数据：
     * 1.分割线
     * 2.手写签名
     * 3.选择数据、查询
     * 4.流水号（提交后系统生成）
     *
     * @param insertBatchRequest insertBatchRequest
     * @return 新建成功的数据编号
     */
    List<String> insertBatch(JdyDataSaveBatchRequest insertBatchRequest);

    /**
     * 修改单条数据
     * 注：使用 API 添加数据时，会触发的事件有新数据提交提醒、聚合表计算&校验、数据操作日志、数据量统计。也可以通过请求参数来控制是否发起流程。但是不会触发重复值校验、必填校验。
     * 另外，系统字段 和以下所列举的字段不支持添加和修改数据：
     * 1.分割线
     * 2.手写签名
     * 3.选择数据、查询
     * 4.流水号（提交后系统生成）
     *
     * @param updateOneRequest updateOneRequest
     * @param ignoreNull       忽略null值,true:null值属性不会请求至简道云,简道云会保持原值处理,false:null至属性会被请求至简道云,简道云会将该属性值清空
     * @return 返回修改后的新数据，内容同查询单条数据接口
     */
    ObjectNode updateOne(JdyUpdateOneRequest updateOneRequest, boolean ignoreNull);

    /**
     * 修改单条数据
     * 注：使用 API 添加数据时，会触发的事件有新数据提交提醒、聚合表计算&校验、数据操作日志、数据量统计。也可以通过请求参数来控制是否发起流程。但是不会触发重复值校验、必填校验。
     * 另外，系统字段 和以下所列举的字段不支持添加和修改数据：
     * 1.分割线
     * 2.手写签名
     * 3.选择数据、查询
     * 4.流水号（提交后系统生成）
     *
     * @param updateOneRequest updateOneRequest
     * @param ignoreNull       忽略null值,true:null值属性不会请求至简道云,简道云会保持原值处理,false:null至属性会被请求至简道云,简道云会将该属性值清空
     * @param valueType        反序列化
     * @return 返回修改后的新数据，内容同查询单条数据接口
     */
    <T> T updateOne(JdyUpdateOneRequest updateOneRequest, boolean ignoreNull, Class<T> valueType);

    /**
     * 修改多条数据
     * 注：
     * 1.修改多条数据接口暂不支持子表单。
     * 2.附件和图片字段更新时会清除字段中原有的文件。
     * 3.修改多条数据是指把多条数据的字段修改成一个固定值。
     *
     * @param updateBatchRequest updateBatchRequest
     * @param ignoreNull         忽略null值,true:null值属性不会请求至简道云,简道云会保持原值处理,false:null至属性会被请求至简道云,简道云会将该属性值清空
     * @return 修改成功的数据数量
     */
    int updateBatch(JdyUpdateBatchRequest updateBatchRequest, boolean ignoreNull);

    /**
     * 删除单条数据
     *
     * @param deleteOneRequest deleteOneRequest
     * @return 删除结果
     */
    boolean deleteOne(JdyDeleteOneRequest deleteOneRequest);

    /**
     * 删除多条数据
     *
     * @param deleteBatchRequest deleteBatchRequest
     * @return 删除成功数据数量
     */
    int deleteBatch(JdyDeleteBatchRequest deleteBatchRequest);
}
