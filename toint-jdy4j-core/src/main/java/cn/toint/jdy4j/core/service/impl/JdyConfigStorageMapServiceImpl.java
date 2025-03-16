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

import cn.toint.jdy4j.core.model.JdyConfigStorage;
import cn.toint.jdy4j.core.service.JdyConfigStorageService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.extra.validation.ValidationUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Toint
 * @date 2024/10/19
 */
@Slf4j
public class JdyConfigStorageMapServiceImpl implements JdyConfigStorageService {
    /**
     * 简道云配置Jdk缓存
     */
    private final Map<String, JdyConfigStorage> jdyConfigStorageJdkMap = new ConcurrentHashMap<>();

    @Override
    public JdyConfigStorage putJdyConfigStorage(final JdyConfigStorage jdyConfigStorage) {
        ValidationUtil.validateAndThrowFirst(jdyConfigStorage);
        this.jdyConfigStorageJdkMap.put(jdyConfigStorage.getCorpName(), jdyConfigStorage);
        return jdyConfigStorage;
    }

    @Override
    public JdyConfigStorage getJdyConfigStorage(final String corpName) {
        Assert.notBlank(corpName, "corpName must not be blank");
        return this.jdyConfigStorageJdkMap.get(corpName);
    }

    @Override
    public void deleteJdyConfigStorage(final String corpName) {
        Assert.notBlank(corpName, "corpName can not be blank");
        this.jdyConfigStorageJdkMap.remove(corpName);
    }
}
