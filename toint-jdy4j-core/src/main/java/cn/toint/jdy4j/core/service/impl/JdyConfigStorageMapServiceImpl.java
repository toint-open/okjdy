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
