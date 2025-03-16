package cn.toint.jdy4j.core.util;

import cn.toint.jdy4j.core.client.JdyClient;
import cn.toint.jdy4j.core.model.JdyConfigStorage;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.Assert;

import java.util.function.Function;

/**
 * @author Toint
 * @date 2024/12/28
 */
@Slf4j
public abstract class BaseJdyClientUtil {

    protected volatile static boolean SHUTDOWN = false;

    protected static JdyClient get(final JdyConfigStorage jdyConfigStorage) {
        Assert.notNull(jdyConfigStorage, "jdyConfigStorage must not be null");
        Assert.isFalse(SHUTDOWN, "jdyClient is shutdown");

        try {
            return JdyClient.get(jdyConfigStorage.getCorpName());
        } catch (Exception e) {
            JdyClient.get().registerJdyConfigStorage(jdyConfigStorage);
            return JdyClient.get(jdyConfigStorage.getCorpName());
        }
    }

    public static <R> R execute(final JdyConfigStorage jdyConfigStorage, final Function<JdyClient, R> function) {
        try (final JdyClient jdyClient = BaseJdyClientUtil.get(jdyConfigStorage)) {
            return function.apply(jdyClient);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @PreDestroy
    public abstract void shutdown();

    protected static void shutdown(final JdyConfigStorage jdyConfigStorage) {
        SHUTDOWN = true;
        JdyClient.get().deleteJdyConfigStorage(jdyConfigStorage.getCorpName());
        log.info("jdyClient shutdown success");
    }
}
