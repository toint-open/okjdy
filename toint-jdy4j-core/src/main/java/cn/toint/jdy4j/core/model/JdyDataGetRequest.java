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
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JdyDataGetRequest {
    /**
     * 应用id
     */
    @JsonProperty("app_id")
    @NotBlank
    private String appId;

    /**
     * 表单id
     */
    @JsonProperty("entry_id")
    @NotBlank
    private String entryId;

    /**
     * 数据id
     */
    @JsonProperty("data_id")
    @NotBlank
    private String dataId;

    public JdyDataGetRequest() {
    }

    public JdyDataGetRequest(@Nonnull final String appId, @Nonnull final String entryId, @Nonnull final String dataId) {
        Assert.notBlank(appId, "appId must not be blank");
        Assert.notBlank(entryId, "entryId must not be blank");
        Assert.notBlank(dataId, "dataId must not be blank");
        this.appId = appId;
        this.entryId = entryId;
        this.dataId = dataId;
    }

    public static <T extends JdyDo> JdyDataGetRequest of(@Nonnull final T data) {
        Assert.notNull(data, "data must not be null");
        return new JdyDataGetRequest(data.getAppId(), data.getEntryId(), data.getDataId());
    }

    public static JdyDataGetRequest of(@Nonnull final JsonNode data) {
        Assert.isFalse(JacksonUtil.isNull(data), "data must not be null");
        final JdyDo jdyDo = JacksonUtil.treeToValue(data, JdyDo.class);
        return new JdyDataGetRequest(jdyDo.getAppId(), jdyDo.getEntryId(), jdyDo.getDataId());
    }
}
