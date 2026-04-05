package com.erp.common.aspect;

import com.erp.common.annotation.DataPermission;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 数据权限占位实现：当前不做拦截，仅保留切入点，便于后续接入组织/部门/角色的数据范围。
 */
@Aspect
@Component
public class DataPermissionAspect {

    private static final Logger log = LoggerFactory.getLogger(DataPermissionAspect.class);

    @Around("@within(dataPermission) || @annotation(dataPermission)")
    public Object around(ProceedingJoinPoint pjp, DataPermission dataPermission) throws Throwable {
        // TODO: 接入数据范围过滤（组织/部门/个人）
        if (dataPermission != null && dataPermission.resource() != null && !dataPermission.resource().isBlank()) {
            log.debug("DataPermission placeholder: resource={} method={}", dataPermission.resource(), pjp.getSignature());
        }
        return pjp.proceed();
    }
}
