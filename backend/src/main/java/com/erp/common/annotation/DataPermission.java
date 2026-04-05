package com.erp.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限占位注解：后续可用于标注需要做数据权限过滤的接口/方法。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {
    /**
     * 资源编码（如：SYS_USER、INV_STOCK等），用于后续映射权限策略。
     */
    String resource() default "";
}
