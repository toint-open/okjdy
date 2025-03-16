package cn.toint.jdy4j.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 简道云表单信息
 *
 * @author Toint
 * @date 2024/11/20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JdyTable {
    /**
     * 简道云表单appId
     */
    String appId();

    /**
     * 简道云表单entryId
     */
    String entryId();
}