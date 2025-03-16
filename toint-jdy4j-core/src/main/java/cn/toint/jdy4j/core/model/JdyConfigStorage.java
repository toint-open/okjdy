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

import cn.toint.jdy4j.core.constant.JdyConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.lang.Assert;

import java.io.Serializable;

/**
 * 简道云配置存储对象
 *
 * @author Toint
 * @date 2024/10/19
 */
@Data
@NoArgsConstructor
public class JdyConfigStorage implements Serializable {
    /**
     * 企业名称
     */
    @NotBlank
    private String corpName;
    /**
     * 简道云开放平台密钥
     */
    @NotBlank
    private String apiKey;
    /**
     * 简道云服务地址 <br>
     * 1. 需要声明协议与主机,如: <a href="https://www.toint.cn">https://www.toint.cn</a> <br>
     * 2. 不允许携带路径信息
     */
    @NotBlank
    private String serverUrl = JdyConstant.DEFAULT_SERVER_URL;

    public JdyConfigStorage(final String corpName, final String apiKey, final String serverUrl) {
        Assert.isFalse(StringUtils.isAnyBlank(corpName, apiKey, serverUrl));
        this.corpName = corpName;
        this.apiKey = apiKey;
        this.serverUrl = serverUrl;
    }

    /**
     * @param corpName  企业名称
     * @param apiKey    简道云开放平台密钥
     * @param serverUrl 简道云服务地址
     * @return 简道云配置存储对象
     */
    public static JdyConfigStorage of(final String corpName, final String apiKey, final String serverUrl) {
        return new JdyConfigStorage(corpName, apiKey, serverUrl);
    }

    /**
     * @param corpName 企业名称
     * @param apiKey   简道云开放平台密钥
     * @return 简道云配置存储对象
     */
    public static JdyConfigStorage of(final String corpName, final String apiKey) {
        return new JdyConfigStorage(corpName, apiKey, JdyConstant.DEFAULT_SERVER_URL);
    }

}
