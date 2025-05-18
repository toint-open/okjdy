package cn.toint.jdy4j.core.exception;

/**
 * 简道云请求超出频率异常
 *
 * @author Toint
 * @date 2025/5/18
 */
public class JdyRequestLimitException extends RuntimeException {
    public JdyRequestLimitException(final String message) {
        super(message);
    }
}
