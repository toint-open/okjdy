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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.hutool.core.collection.CollUtil;

import java.util.Collection;
import java.util.List;

/**
 * @author Toint
 * @date 2025/3/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JdyDeleteBatchRequest {
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
     * 是否必填:是
     */
    @JsonProperty("data_ids")
    @NotNull
    @NotEmpty
    private Collection<String> dataIds;

    public static JdyDeleteBatchRequest of(final String appId, final String entryId, final Collection<String> dataIds) {
        final JdyDeleteBatchRequest deleteBatchRequest = new JdyDeleteBatchRequest();
        deleteBatchRequest.setAppId(appId);
        deleteBatchRequest.setEntryId(entryId);
        deleteBatchRequest.setDataIds(dataIds);
        return deleteBatchRequest;
    }

    public static JdyDeleteBatchRequest of(final Iterable<?> datas) {
        List<BaseJdyTable> jdyTables = JacksonUtil.convertValue(datas, new TypeReference<>() {
        });
        return JdyDeleteBatchRequest.of(
                jdyTables.getFirst().getAppId(),
                jdyTables.getFirst().getEntryId(),
                CollUtil.map(jdyTables, BaseJdyTable::getDataId)
        );
    }
}
