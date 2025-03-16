/**
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

import cn.toint.jdy4j.core.util.JacksonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Toint
 * @date 2025/3/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JdyUpdateOneRequest {
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
    @JsonProperty("data_id")
    @NotBlank
    private String dataId;
    /**
     * 数据对象
     * 是否必填:是
     */
    @JsonProperty("data")
    @NotNull
    private JsonNode data;
    /**
     * 是否触发智能助手
     * 是否必填:否
     */
    @JsonProperty("is_start_trigger")
    private boolean startTrigger = true;
    /**
     * 事务ID；transaction_id 用于绑定一批上传的文件，若数据中包含附件或图片控件，则 transaction_id 必须与“获取文件上传凭证和上传地址接口”中的 transaction_id 参数相同。
     * 是否必填:否
     */
    @JsonProperty("transaction_id")
    private String transactionId;

    public static JdyUpdateOneRequest of(final String appId, final String entryId, final String dataId, final JsonNode data) {
        final JdyUpdateOneRequest updateOneRequest = new JdyUpdateOneRequest();
        updateOneRequest.setAppId(appId);
        updateOneRequest.setEntryId(entryId);
        updateOneRequest.setDataId(dataId);
        updateOneRequest.setData(data);
        return updateOneRequest;
    }

    public static JdyUpdateOneRequest of(final Object data) {
        final BaseJdyTable jdyTable = JacksonUtil.convertValue(data, BaseJdyTable.class);
        return JdyUpdateOneRequest.of(jdyTable.getAppId(), jdyTable.getEntryId(), jdyTable.getDataId(), JacksonUtil.valueToTree(data));
    }

    public JdyUpdateOneRequest transactionId(final String transactionId) {
        this.transactionId = transactionId;
        return this;
    }
}
