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
package cn.toint.jdy4j.core.config;

import cn.toint.jdy4j.core.service.*;
import cn.toint.jdy4j.core.service.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author Toint
 * @date 2024/10/19
 */
@AutoConfiguration
@Slf4j
public class JdyAutoConfig {
    /**
     * 简道云请求
     */
    @Bean
    public JdyRequestService jdyRequestService() {
        return new JdyRequestServiceImpl();
    }

    /**
     * 简道云流程
     */
    @Bean
    public JdyFlowService jdyFlowService() {
        return new JdyFlowServiceImpl();
    }

    /**
     * 简道云文件
     */
    @Bean
    public JdyFileService jdyFileService() {
        return new JdyFileServiceImpl();
    }

    /**
     * 简道云数据
     */
    @Bean
    public JdyDataService jdyDataService() {
        return new JdyDataServiceImpl();
    }

    /**
     * 简道云通讯录
     */
    @Bean
    public JdyCorpService jdyCorpService() {
        return new JdyCorpServiceImpl();
    }

    /**
     * 简道云应用
     */
    @Bean
    public JdyAppService jdyAppService() {
        return new JdyAppServiceImpl();
    }

    /**
     * 简道云配置存储缓存
     */
    @Bean
    public JdyConfigStorageService jdyConfigStorageMapService() {
        return new JdyConfigStorageMapServiceImpl();
    }

    /**
     * 简道云配置存储缓存
     */
    @Bean
    @ConditionalOnClass(StringRedisTemplate.class)
    @Primary
    public JdyConfigStorageService jdyConfigStorageRedisService() {
        return new JdyConfigStorageRedisServiceImpl();
    }
}