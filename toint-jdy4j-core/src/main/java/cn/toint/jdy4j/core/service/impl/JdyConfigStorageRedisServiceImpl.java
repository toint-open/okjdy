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

package cn.toint.jdy4j.core.service.impl;

import cn.toint.jdy4j.core.constant.JdyConstant;
import cn.toint.jdy4j.core.model.JdyConfigStorage;
import cn.toint.jdy4j.core.service.JdyConfigStorageService;
import cn.toint.tool.util.JacksonUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.extra.validation.ValidationUtil;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;

/**
 * @author Toint
 * @date 2025/3/3
 */
@Slf4j
public class JdyConfigStorageRedisServiceImpl implements JdyConfigStorageService {
    /**
     * redis api
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 简道云配置存储key
     */
    private final String jdyConfigStorageKey = JdyConstant.PROJECT_NAME + ":jdy-config-storage";

    @Override
    public JdyConfigStorage putJdyConfigStorage(final JdyConfigStorage jdyConfigStorage) {
        ValidationUtil.validateAndThrowFirst(jdyConfigStorage);
        this.getOpsForHash().put(this.jdyConfigStorageKey, jdyConfigStorage.getCorpName(), JacksonUtil.writeValueAsString(jdyConfigStorage));
        return jdyConfigStorage;
    }

    @Override
    public JdyConfigStorage getJdyConfigStorage(final String corpName) {
        if (StringUtils.isBlank(corpName)) {
            return null;
        }

        return Optional.ofNullable(this.getOpsForHash().get(this.jdyConfigStorageKey, corpName))
                .map(str -> JacksonUtil.tryReadValue(str, JdyConfigStorage.class))
                .orElse(null);
    }

    @Override
    public void deleteJdyConfigStorage(final String corpName) {
        Assert.notBlank(corpName, "corpName can not be blank");
        this.getOpsForHash().delete(this.jdyConfigStorageKey, corpName);
    }

    // ===============
    private HashOperations<String, String, String> getOpsForHash() {
        return this.stringRedisTemplate.opsForHash();
    }
}
