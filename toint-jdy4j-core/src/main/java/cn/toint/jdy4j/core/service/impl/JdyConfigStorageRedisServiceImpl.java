package cn.toint.jdy4j.core.service.impl;

import cn.toint.jdy4j.core.constant.JdyConstant;
import cn.toint.jdy4j.core.model.JdyConfigStorage;
import cn.toint.jdy4j.core.service.JdyConfigStorageService;
import cn.toint.tool.json.JacksonUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.extra.validation.ValidationUtil;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Toint
 * @date 2025/3/3
 */
@Service
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
