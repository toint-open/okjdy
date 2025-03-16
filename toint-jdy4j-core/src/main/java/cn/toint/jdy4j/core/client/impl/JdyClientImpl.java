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
package cn.toint.jdy4j.core.client.impl;

import cn.toint.jdy4j.core.client.JdyClient;
import cn.toint.jdy4j.core.model.JdyConfigStorage;
import cn.toint.jdy4j.core.service.JdyAppService;
import cn.toint.jdy4j.core.service.JdyConfigStorageService;
import cn.toint.jdy4j.core.service.JdyDataService;
import cn.toint.jdy4j.core.service.JdyFileService;
import cn.toint.jdy4j.core.util.JdyConfigStorageHolder;
import cn.toint.jdy4j.core.util.JdyWidgetHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.Assert;

/**
 * @author Toint
 * @date 2024/10/19
 */
@Slf4j
public class JdyClientImpl implements JdyClient {
    /**
     * 简道云配置
     */
    @Resource
    private JdyConfigStorageService jdyConfigStorageService;

    /**
     * 简道云应用
     */
    @Resource
    private JdyAppService jdyAppService;

    /**
     * 简道云数据
     */
    @Resource
    private JdyDataService jdyDataService;

    /**
     * 简道云文件
     */
    @Resource
    private JdyFileService jdyFileService;


    @Override
    public JdyConfigStorage putJdyConfigStorage(final JdyConfigStorage jdyConfigStorage) {
        return this.jdyConfigStorageService.putJdyConfigStorage(jdyConfigStorage);
    }

    @Override
    public void deleteJdyConfigStorage(final String corpName) {
        this.jdyConfigStorageService.deleteJdyConfigStorage(corpName);
    }

    @Override
    public boolean switchover(final String corpName) {
        Assert.notBlank(corpName, "corpName must not be blank");

        // 是否已注册配置
        if (!this.jdyConfigStorageService.containsJdyConfigStorage(corpName)) {
            return false;
        }

        JdyConfigStorageHolder.set(corpName);
        return true;
    }

    @Override
    public JdyClient switchoverTo(final String corpName) {
        Assert.isTrue(this.switchover(corpName), "当前企业未注册简道云配置");
        return this;
    }

    @Override
    public JdyAppService getJdyAppService() {
        return this.jdyAppService;
    }

    @Override
    public JdyDataService getJdyDataService() {
        return this.jdyDataService;
    }

    @Override
    public JdyFileService getJdyFileService() {
        return this.jdyFileService;
    }

    @Override
    public void close() {
        JdyConfigStorageHolder.remove();
        JdyWidgetHolder.remove();
    }
}
