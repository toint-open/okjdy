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
package cn.toint.okjdy.core.model;

import cn.toint.oktool.util.Assert;
import cn.toint.oktool.util.JacksonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Toint
 * @date 2025/3/15
 */
@Data
public class JdyDataDeleteBatchRequest {
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
    @JsonProperty("data_ids")
    @NotEmpty
    private Collection<String> dataIds;

    public JdyDataDeleteBatchRequest() {
    }

    public JdyDataDeleteBatchRequest(final String appId, final String entryId, final Collection<String> dataIds) {
        Assert.notBlank(appId, "appId must not be blank");
        Assert.notBlank(entryId, "entryId must not be blank");
        Assert.notEmpty(dataIds, "dataIds must not be empty");
        this.appId = appId;
        this.entryId = entryId;
        this.dataIds = dataIds;
    }

    public static JdyDataDeleteBatchRequest of(final JsonNode data) {
        Assert.notNull(data, "data must not be null");
        Assert.isTrue(data.isArray(), "data must not be array");

        final List<JdyDo> jdyDos = JacksonUtil.treeToValue(data, new TypeReference<>() {
        });

        return JdyDataDeleteBatchRequest.of(jdyDos);
    }

    public static <T extends JdyDo> JdyDataDeleteBatchRequest of(final List<T> data) {
        Assert.notEmpty(data, "data must not be empty");

        final Set<String> dataIds = data.stream()
                .map(JdyDo::getDataId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        return new JdyDataDeleteBatchRequest(data.getFirst().getAppId(), data.getFirst().getEntryId(), dataIds);
    }
}
