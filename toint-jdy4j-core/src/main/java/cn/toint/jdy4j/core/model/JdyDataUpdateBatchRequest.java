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
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;

/**
 * @author Toint
 * @date 2025/3/15
 */
@Data
public class JdyDataUpdateBatchRequest {
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
    @NotNull
    private Collection<String> dataIds;

    /**
     * 数据(暂不支持子表单)
     */
    @JsonProperty("data")
    @NotNull
    private JsonNode data;

    /**
     * 事务ID；transaction_id 用于绑定一批上传的文件，若数据中包含附件或图片控件，则 transaction_id 必须与“获取文件上传凭证和上传地址接口”中的 transaction_id 参数相同。
     */
    @JsonProperty("transaction_id")
    private String transactionId;

    public JdyDataUpdateBatchRequest() {
    }

    public JdyDataUpdateBatchRequest(final String appId, final String entryId, final Collection<String> dataIds, final JsonNode data) {
        Assert.notBlank(appId, "appId can not be blank");
        Assert.notBlank(entryId, "entryId can not be blank");
        Assert.notEmpty(dataIds, "dataIds can not be null");
        Assert.notNull(data, "data can not be null");
        this.appId = appId;
        this.entryId = entryId;
        this.dataIds = dataIds;
        this.data = data;
    }

    public static JdyDataUpdateBatchRequest of(final JsonNode data, final Collection<String> dataIds) {
        Assert.notNull(data, "data can not be null");
        Assert.notEmpty(dataIds, "dataIds can not be null");
        final JdyDo jdyDo = JacksonUtil.treeToValue(data, JdyDo.class);
        return new JdyDataUpdateBatchRequest(jdyDo.getAppId(), jdyDo.getEntryId(), dataIds, data);
    }

    public static <T extends JdyDo> JdyDataUpdateBatchRequest of(final T data, final Collection<String> dataIds) {
        Assert.notNull(data, "data can not be null");
        Assert.notEmpty(dataIds, "dataIds can not be null");
        return new JdyDataUpdateBatchRequest(data.getAppId(), data.getEntryId(), dataIds, JacksonUtil.valueToTree(data));
    }

    public JdyDataUpdateBatchRequest transactionId(final String transactionId) {
        this.transactionId = transactionId;
        return this;
    }
}
