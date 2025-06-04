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
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Toint
 * @date 2025/3/15
 */
@Data
public class JdyDataUpdateRequest {
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
     * 数据对象
     */
    @JsonProperty("data")
    @NotNull
    private JsonNode data;

    /**
     * 是否触发智能助手
     */
    @JsonProperty("is_start_trigger")
    private boolean startTrigger = true;

    /**
     * 事务ID；transaction_id 用于绑定一批上传的文件，若数据中包含附件或图片控件，则 transaction_id 必须与“获取文件上传凭证和上传地址接口”中的 transaction_id 参数相同。
     * 是否必填:否
     */
    @JsonProperty("transaction_id")
    private String transactionId;

    public JdyDataUpdateRequest() {
    }

    public JdyDataUpdateRequest(@Nonnull final String appId, @Nonnull final String entryId, @Nonnull final String dataId, @Nonnull final JsonNode data) {
        Assert.notBlank(appId, "appId must not be blank");
        Assert.notBlank(entryId, "entryId must not be blank");
        Assert.notBlank(dataId, "dataId must not be blank");
        Assert.notNull(data, "data must not be null");
        this.appId = appId;
        this.entryId = entryId;
        this.dataId = dataId;
        this.data = data;
    }

    public static JdyDataUpdateRequest of(@Nonnull final JsonNode data) {
        Assert.notNull(data, "data must not be null");
        final JdyDo jdyDo = JacksonUtil.treeToValue(data, JdyDo.class);
        return new JdyDataUpdateRequest(jdyDo.getAppId(), jdyDo.getEntryId(), jdyDo.getDataId(), data);
    }

    public static <T extends JdyDo> JdyDataUpdateRequest of(@Nonnull final T data) {
        Assert.notNull(data, "data must not be null");
        return new JdyDataUpdateRequest(data.getAppId(), data.getEntryId(), data.getDataId(), JacksonUtil.valueToTree(data));
    }

    public JdyDataUpdateRequest transactionId(final String transactionId) {
        this.transactionId = transactionId;
        return this;
    }
}
