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

import java.util.Collection;

/**
 * 新建多条数据
 */
@Data
public class JdyDataSaveBatchRequest {
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
     * 数据内容
     */
    @JsonProperty("data_list")
    @NotNull
    private JsonNode datas;
    /**
     * 数据提交人（取成员编号 username，可从通讯录接口获取）
     */
    @JsonProperty("data_creator")
    private String dataCreator;

    /**
     * 是否发起流程（仅流程表单有效）
     */
    @JsonProperty("is_start_workflow")
    private boolean startWorkflow = true;

    /**
     * 事务ID；transaction_id 用于绑定一批上传的文件，若数据中包含附件或图片控件，则 transaction_id 必须与“获取文件上传凭证和上传地址接口”中的 transaction_id 参数相同。
     */
    @JsonProperty("transaction_id")
    private String transactionId;

    public JdyDataSaveBatchRequest() {
    }

    public JdyDataSaveBatchRequest(@Nonnull final String appId, @Nonnull final String entryId, @Nonnull final JsonNode datas) {
        Assert.notBlank(appId, "appId must not be blank");
        Assert.notBlank(entryId, "entryId must not be blank");
        Assert.notNull(datas, "datas must not be null");
        this.appId = appId;
        this.entryId = entryId;
        this.datas = datas;
    }

    public static JdyDataSaveBatchRequest of(@Nonnull final JsonNode datas) {
        Assert.notEmpty(datas, "datas must not be empty");
        final JdyDo jdyDo = JacksonUtil.treeToValue(datas.get(0), JdyDo.class);
        return new JdyDataSaveBatchRequest(jdyDo.getAppId(), jdyDo.getEntryId(), datas);
    }

    public static <T extends JdyDo> JdyDataSaveBatchRequest of(final Collection<T> datas) {
        Assert.notEmpty(datas, "datas must not be empty");
        return JdyDataSaveBatchRequest.of(JacksonUtil.valueToTree(datas));
    }

    public JdyDataSaveBatchRequest transactionId(final String transactionId) {
        Assert.notBlank(transactionId, "transactionId must not be blank");
        this.transactionId = transactionId;
        return this;
    }
}
