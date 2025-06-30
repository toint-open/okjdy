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

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Toint
 * @date 2025/3/15
 */
@Data
public class JdyFileUploadResponse {
    /**
     * keys
     */
    @NotEmpty
    private Map<File, String> fileKeyMap = new HashMap<>();

    /**
     * 事务
     */
    @NotBlank
    private String transactionId;

    @Nonnull
    public JdyFile toJdyFile() {
        JdyFile jdyFile = new JdyFile();
        for (final String value : fileKeyMap.values()) {
            final JdyFile.Detail detail = new JdyFile.Detail();
            detail.setKey(value);
            jdyFile.add(detail);
        }
        return jdyFile;
    }
}

