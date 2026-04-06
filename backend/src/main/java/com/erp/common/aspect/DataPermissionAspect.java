package com.erp.common.aspect;

import com.erp.common.annotation.DataPermission;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataPermissionAspect {

    private static final Logger log = LoggerFactory.getLogger(DataPermissionAspect.class);

    @Around("@within(dataPermission) || @annotation(dataPermission)")
    public Object around(ProceedingJoinPoint pjp, DataPermission dataPermission) throws Throwable {
        if (dataPermission != null) {
            // 占位实现：后续可在此处接入 SQL 条件拼接（本人/本部门/本组织/指定组织/全部）
            log.debug("DataPermission placeholder resource={} method={}", dataPermission.resource(), pjp.getSignature());
        }
        return pjp.proceed();
    }
}
