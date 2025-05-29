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
package cn.toint.jdy4j.core.client;

import cn.toint.jdy4j.core.client.impl.BaseJdyClientImpl;
import cn.toint.jdy4j.core.model.JdyConfigStorage;
import cn.toint.jdy4j.core.service.JdyAppService;
import cn.toint.jdy4j.core.service.JdyConfigStorageService;
import cn.toint.jdy4j.core.service.JdyDataService;
import cn.toint.jdy4j.core.service.JdyFileService;
import cn.toint.jdy4j.core.util.JdyConfigStorageHolder;
import cn.toint.jdy4j.core.util.JdyWidgetHolder;
import cn.toint.tool.util.Assert;

import java.util.function.Function;

/**
 * 简道云客户端
 *
 * <p>1. 建议开发者 extends {@link BaseJdyClientImpl}</p>
 *
 * @author Toint
 * @date 2024/10/19
 */
public interface JdyClient extends AutoCloseable {

    /**
     * 执行方法, 默认自动关闭资源
     *
     * @param function 执行方法
     * @return result
     */
    default <R> R execute(final Function<JdyClient, R> function) {
        return this.execute(function, true);
    }

    /**
     * 执行方法
     *
     * @param function  执行方法
     * @param autoClose 是否自动关闭资源
     * @return result
     */
    default <R> R execute(final Function<JdyClient, R> function, final boolean autoClose) {
        final JdyConfigStorage jdyConfigStorage = this.getJdyConfigStorage();
        Assert.notNull(jdyConfigStorage, "jdyConfigStorage must not be null");
        final String corpName = jdyConfigStorage.getCorpName();
        Assert.notBlank(corpName, "corpName must not be blank");

        // 初始化配置
        final JdyConfigStorageService jdyConfigStorageService = this.getJdyConfigStorageService();
        if (!jdyConfigStorageService.containsJdyConfigStorage(corpName)) {
            jdyConfigStorageService.putJdyConfigStorage(jdyConfigStorage);
        }

        // 切换配置
        JdyConfigStorageHolder.set(corpName);

        try {
            return function.apply(this);
        } finally {
            if (autoClose) {
                close();
            }
        }
    }

    /**
     * 获取配置, 开发者需实现该接口
     */
    JdyConfigStorage getJdyConfigStorage();

    @Override
    default void close() {
        JdyConfigStorageHolder.remove();
        JdyWidgetHolder.remove();
    }

    /**
     * 简道云应用
     */
    JdyAppService getJdyAppService();

    /**
     * 简道云数据
     */
    JdyDataService getJdyDataService();

    /**
     * 简道云文件
     */
    JdyFileService getJdyFileService();

    /**
     * 获取简道云配置服务
     */
    JdyConfigStorageService getJdyConfigStorageService();
}