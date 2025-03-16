package cn.toint.jdy4j.core.service;

import cn.toint.jdy4j.core.model.JdyConfigStorage;

import java.util.Objects;
import java.util.Optional;

/**
 * 简道云配置存储
 *
 * @author Toint
 * @date 2024/10/19
 */
public interface JdyConfigStorageService {
    /**
     * 添加简道云配置存储
     *
     * @param jdyConfigStorage 简道云配置存储对象
     * @return 简道云配置存储对象
     */
    JdyConfigStorage putJdyConfigStorage(final JdyConfigStorage jdyConfigStorage);

    /**
     * 配置是否存在
     *
     * @param corpName 企业名称
     * @return true:存在,false:不存在
     */
    default boolean containsJdyConfigStorage(final String corpName) {
        return Objects.nonNull(this.getJdyConfigStorage(corpName));
    }

    /**
     * 获取配置
     *
     * @param corpName 企业名称
     * @return 简道云配置
     */
    JdyConfigStorage getJdyConfigStorage(final String corpName);

    /**
     * 获取配置
     *
     * @param corpName 企业名称
     * @return 简道云配置
     * @throws RuntimeException 配置不存在
     */
    default JdyConfigStorage getJdyConfigStorageRequire(final String corpName) {
        return Optional.ofNullable(this.getJdyConfigStorage(corpName))
                .orElseThrow(() -> new RuntimeException("企业未注册简道云配置信息, corpName: " + corpName));
    }

    /**
     * 删除配置
     *
     * @param corpName 企业名称
     */
    void deleteJdyConfigStorage(final String corpName);
}
