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
package cn.toint.okjdy.core.client;

import cn.toint.okjdy.core.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * 简道云客户端
 *
 * @author Toint
 * @date 2024/10/19
 */
public interface JdyClient {
    /**
     * 获取用户应用
     */
    @Nonnull
    List<JdyApp> listApp(@Nonnull JdyAppListRequest jdyAppListRequest);

    /**
     * 获取用户表单
     */
    @Nonnull
    List<JdyEntry> listEntry(@Nonnull JdyEntryListRequest jdyEntryListRequest);

    /**
     * 获取表单字段
     */
    @Nonnull
    JdyFieldListResponse listField(@Nonnull JdyFieldListRequest jdyFieldListRequest);

    /**
     * 查询数据
     */
    @Nullable
    JsonNode getData(@Nonnull JdyDataGetRequest jdyDataGetRequest);

    /**
     * 查询数据
     */
    @Nullable
    <T extends JdyDo> T getData(@Nonnull JdyDataGetRequest jdyDataGetRequest, @Nonnull Class<T> responseClass);

    /**
     * 查询数据列表
     * 该接口的返回数据, 始终按照数据 ID 正序排列.
     * 若要设置循环调取数据, 可以利用 data_id 字段来设置参数避免调取重复数据.
     * 如需要查询 230 条数据:
     * 第一次查询时可以不传 data_id 字段, 若设置 limit 为 100 , 则第一次返回了前 100 条数据；
     * 第二次, 用第 100 条数据的 data_id 进行查询, 若设置 limit 为100, 则第二次返回 101～200 这 100 条数据；
     * 第三次, 用第 200 条数据的 data_id 进行查询, 若设置 limit 为100, 则第三次返回 201～230 这 30 条数据.
     * 由于第三次返回结果只有 30 条, 未达到设置的 limit 上限100, 则说明查询结束.
     *
     * @param jdyListDataRequest jdyListRequest
     * @return 数据列表
     */
    @Nonnull
    JsonNode listData(@Nonnull JdyListDataRequest jdyListDataRequest);

    /**
     * 查询数据列表
     * 该接口的返回数据, 始终按照数据 ID 正序排列.
     * 若要设置循环调取数据, 可以利用 data_id 字段来设置参数避免调取重复数据.
     * 如需要查询 230 条数据:
     * 第一次查询时可以不传 data_id 字段, 若设置 limit 为 100 , 则第一次返回了前 100 条数据；
     * 第二次, 用第 100 条数据的 data_id 进行查询, 若设置 limit 为100, 则第二次返回 101～200 这 100 条数据；
     * 第三次, 用第 200 条数据的 data_id 进行查询, 若设置 limit 为100, 则第三次返回 201～230 这 30 条数据.
     * 由于第三次返回结果只有 30 条, 未达到设置的 limit 上限100, 则说明查询结束.
     *
     * @param jdyListDataRequest jdyListRequest
     * @param responseType       返回值类型
     * @return 数据列表
     */
    @Nonnull
    <T extends JdyDo> List<T> listData(@Nonnull JdyListDataRequest jdyListDataRequest, @Nonnull Class<T> responseType);

    /**
     * 查询数据列表
     * 该接口的返回数据, 始终按照数据 ID 正序排列.
     * 若要设置循环调取数据, 可以利用 data_id 字段来设置参数避免调取重复数据.
     * 如需要查询 230 条数据:
     * 第一次查询时可以不传 data_id 字段, 若设置 limit 为 100 , 则第一次返回了前 100 条数据；
     * 第二次, 用第 100 条数据的 data_id 进行查询, 若设置 limit 为100, 则第二次返回 101～200 这 100 条数据；
     * 第三次, 用第 200 条数据的 data_id 进行查询, 若设置 limit 为100, 则第三次返回 201～230 这 30 条数据.
     * 由于第三次返回结果只有 30 条, 未达到设置的 limit 上限100, 则说明查询结束.
     *
     * @param jdyListDataRequest jdyListRequest
     * @param predicate          结果返回策略 (predicate 入参为本次查询结果, 非空), true: 返回结果, false: 忽略结果
     * @return 数据列表
     */
    @Nonnull
    JsonNode listData(@Nonnull JdyListDataRequest jdyListDataRequest, @Nullable Predicate<JsonNode> predicate);

    /**
     * 查询数据列表
     * 该接口的返回数据, 始终按照数据 ID 正序排列.
     * 若要设置循环调取数据, 可以利用 data_id 字段来设置参数避免调取重复数据.
     * 如需要查询 230 条数据:
     * 第一次查询时可以不传 data_id 字段, 若设置 limit 为 100 , 则第一次返回了前 100 条数据；
     * 第二次, 用第 100 条数据的 data_id 进行查询, 若设置 limit 为100, 则第二次返回 101～200 这 100 条数据；
     * 第三次, 用第 200 条数据的 data_id 进行查询, 若设置 limit 为100, 则第三次返回 201～230 这 30 条数据.
     * 由于第三次返回结果只有 30 条, 未达到设置的 limit 上限100, 则说明查询结束.
     *
     * @param jdyListDataRequest jdyListRequest
     * @param responseType       返回值类型
     * @param predicate          结果返回策略 (predicate 入参为本次查询结果, 非空), true: 返回结果, false: 忽略结果
     * @return 数据列表
     */
    @Nonnull
    <T extends JdyDo> List<T> listData(@Nonnull JdyListDataRequest jdyListDataRequest, @Nonnull Class<T> responseType, @Nullable Predicate<JsonNode> predicate);

    /**
     * 新增数据
     *
     * @param jdyDataSaveRequest jdyDataSaveRequest
     * @return 返回提交后的完整数据，内容同查询单条数据接口
     * @throws RuntimeException 新增数据失败
     */
    @Nonnull
    JsonNode saveData(@Nonnull JdyDataSaveRequest jdyDataSaveRequest);

    /**
     * 新增数据
     *
     * @param jdyDataSaveRequest jdyDataSaveRequest
     * @return 返回提交后的完整数据，内容同查询单条数据接口
     * @throws RuntimeException 新增数据失败
     */
    @Nonnull
    <T> T saveData(@Nonnull JdyDataSaveRequest jdyDataSaveRequest, @Nonnull Class<T> responseClass);

    /**
     * 新增数据
     *
     * @param jdyDataSaveBatchRequest jdyDataSaveRequest
     * @return 创建成功的数据的 ID 列表
     */
    @Nonnull
    List<String> saveBatchData(@Nonnull JdyDataSaveBatchRequest jdyDataSaveBatchRequest);

    /**
     * 修改数据
     *
     * @param jdyDataUpdateRequest jdyDataUpdateRequest
     * @param ignoreNull           是否忽略 null 值, true: null 字段不会更新至简道云; false: null 字段会更新至简道云.
     * @return 新的数据
     */
    @Nonnull
    JsonNode updateData(@Nonnull JdyDataUpdateRequest jdyDataUpdateRequest, boolean ignoreNull);

    /**
     * 修改数据
     *
     * @param jdyDataUpdateRequest jdyDataUpdateRequest
     * @param ignoreNull           是否忽略 null 值, true: null 字段不会更新至简道云; false: null 字段会更新至简道云.
     * @param responseType         返回值类型
     * @return 新的数据
     */
    @Nonnull
    <T extends JdyDo> T updateData(@Nonnull JdyDataUpdateRequest jdyDataUpdateRequest, boolean ignoreNull, @Nonnull Class<T> responseType);

    /**
     * 修改数据 (数据编号列表数据均修改为传入的数据)
     *
     * @param jdyDataUpdateBatchRequest jdyDataUpdateBatchRequest
     * @param ignoreNull                是否忽略 null 值, true: null 字段不会更新至简道云; false: null 字段会更新至简道云.
     * @return 修改数据数量
     */
    int updateBatchData(@Nonnull JdyDataUpdateBatchRequest jdyDataUpdateBatchRequest, boolean ignoreNull);

    /**
     * 删除数据
     *
     * @param jdyDataDeleteRequest jdyDataDeleteRequest
     * @return 是否删除成功
     */
    boolean deleteData(@Nonnull JdyDataDeleteRequest jdyDataDeleteRequest);

    /**
     * 删除数据
     *
     * @param jdyDataDeleteBatchRequest jdyDataDeleteBatchRequest
     * @return 删除成功数量
     */
    int deleteBatchData(@Nonnull JdyDataDeleteBatchRequest jdyDataDeleteBatchRequest);

    /**
     * 文件上传
     */
    @Nonnull
    JdyFileUploadResponse uploadFile(@Nonnull JdyFileUploadRequest jdyFileUploadRequest, @Nonnull Collection<File> files);
}