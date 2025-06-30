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
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.dromara.hutool.core.data.id.IdUtil;

/**
 * @author Toint
 * @date 2025/3/15
 */
@Data
public class JdyFileUploadRequest {
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
     * 事务ID；transaction_id 用于绑定一批上传的文件，若数据中包含附件或图片控件，则 transaction_id 必须与“获取文件上传凭证和上传地址接口”中的 transaction_id 参数相同。
     */
    @JsonProperty("transaction_id")
    @NotBlank
    private String transactionId;

    public JdyFileUploadRequest() {
    }

    public JdyFileUploadRequest(final String appId, final String entryId, final String transactionId) {
        Assert.notBlank(appId, "appId must not be blank");
        Assert.notBlank(entryId, "entryId must not be blank");
        Assert.notBlank(transactionId, "transactionId must not be blank");
        this.appId = appId;
        this.entryId = entryId;
        this.transactionId = transactionId;
    }

    public static JdyFileUploadRequest of(final JsonNode data, final String transactionId) {
        Assert.notNull(data, "data must not be null");
        return JdyFileUploadRequest.of(JacksonUtil.treeToValue(data, JdyDo.class), transactionId);
    }

    public static <T extends JdyDo> JdyFileUploadRequest of(final T data, final String transactionId) {
        Assert.notNull(data, "data must not be null");
        return new JdyFileUploadRequest(data.getAppId(), data.getEntryId(), transactionId);
    }

    public static JdyFileUploadRequest of(final JsonNode data) {
        Assert.notNull(data, "data must not be null");
        return JdyFileUploadRequest.of(JacksonUtil.treeToValue(data, JdyDo.class), IdUtil.fastSimpleUUID());
    }

    public static <T extends JdyDo> JdyFileUploadRequest of(final T data) {
        Assert.notNull(data, "data must not be null");
        return new JdyFileUploadRequest(data.getAppId(), data.getEntryId(), IdUtil.fastSimpleUUID());
    }
}

