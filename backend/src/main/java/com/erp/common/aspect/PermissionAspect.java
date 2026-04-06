package com.erp.common.aspect;

import com.erp.auth.service.AuthService;
import com.erp.common.annotation.RequiresPermission;
import com.erp.common.exception.BizException;
import com.erp.common.util.SecurityContextUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class PermissionAspect {

    private final AuthService authService;

    public PermissionAspect(AuthService authService) {
        this.authService = authService;
    }

    @Around("@annotation(requiresPermission)")
    public Object around(ProceedingJoinPoint pjp, RequiresPermission requiresPermission) throws Throwable {
        String username = SecurityContextUtil.getUsername();
        List<String> perms = authService.listPermissionsByUsername(username);
        if (!perms.contains(requiresPermission.value())) {
            throw new BizException(403, "无权限: " + requiresPermission.value());
        }
        return pjp.proceed();
    }
}
