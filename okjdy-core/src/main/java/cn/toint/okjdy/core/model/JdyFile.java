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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

/**
 * @author Toint
 * @date 2025/3/15
 */
public class JdyFile extends ArrayList<JdyFile.Detail> {
    /**
     * @author Toint
     * @date 2025/3/15
     */
    @Data
    public static class Detail {
        /**
         * name
         */
        @JsonProperty("name")
        private String name;

        /**
         * size
         */
        @JsonProperty("size")
        private Long size;

        /**
         * mime
         */
        @JsonProperty("mime")
        private String mime;

        /**
         * url
         */
        @JsonProperty("url")
        private String url;

        /**
         * 文件上传key, 在数据查询时该值不会返回
         * 如果想清空文件, 请将key设置为空字符串或 null
         * 注意: 重新赋值后, 会清空原有文件
         */
        @JsonProperty("key")
        private String key;
    }
}
