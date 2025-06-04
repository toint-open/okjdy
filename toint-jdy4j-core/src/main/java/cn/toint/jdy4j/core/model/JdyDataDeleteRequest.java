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
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Toint
 * @date 2025/3/15
 */
@Data
public class JdyDataDeleteRequest {
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
    @NotBlank
    private String dataId;

    /**
     * 是否触发智能助手
     */
    @JsonProperty("is_start_trigger")
    private boolean startTrigger = true;

    public JdyDataDeleteRequest() {
    }

    public JdyDataDeleteRequest(final String appId, final String entryId, final String dataId) {
        Assert.notBlank(appId, "appId must not be blank");
        Assert.notBlank(entryId, "entryId must not be blank");
        Assert.notBlank(dataId, "dataId must not be blank");
        this.appId = appId;
        this.entryId = entryId;
        this.dataId = dataId;
    }

    public static JdyDataDeleteRequest of(final JsonNode data) {
        Assert.notNull(data, "data must not be null");
        final JdyDo jdyDo = JacksonUtil.treeToValue(data, JdyDo.class);
        return new JdyDataDeleteRequest(jdyDo.getAppId(), jdyDo.getEntryId(), jdyDo.getDataId());
    }

    public static <T extends JdyDo> JdyDataDeleteRequest of(final T data) {
        Assert.notNull(data, "data must not be null");
        return new JdyDataDeleteRequest(data.getAppId(), data.getEntryId(), data.getDataId());
    }
}
