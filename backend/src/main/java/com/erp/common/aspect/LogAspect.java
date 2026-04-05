package com.erp.common.aspect;

import com.erp.common.util.SecurityContextUtil;
import com.erp.system.entity.SysOpLog;
import com.erp.system.service.SysOpLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    private final SysOpLogService opLogService;

    public LogAspect(SysOpLogService opLogService) {
        this.opLogService = opLogService;
    }

    @Around("execution(* com.erp..web..*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();

        HttpServletRequest request = null;
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) request = attrs.getRequest();
        } catch (Exception ignored) {
        }

        String uri = request != null ? request.getRequestURI() : null;
        String httpMethod = request != null ? request.getMethod() : null;
        String username = SecurityContextUtil.getUsername();

        try {
            Object ret = pjp.proceed();
            long cost = System.currentTimeMillis() - start;
            log.info("{} {} cost={}ms", httpMethod, uri, cost);

            SysOpLog op = new SysOpLog();
            op.setOrgId(1L);
            op.setUsername(username);
            op.setUri(uri);
            op.setHttpMethod(httpMethod);
            op.setClassMethod(pjp.getSignature().toShortString());
            op.setCostMs(cost);
            op.setSuccess(1);
            opLogService.saveQuietly(op);

            return ret;
        } catch (Throwable t) {
            long cost = System.currentTimeMillis() - start;
            log.error("{} {} failed cost={}ms msg={}", httpMethod, uri, cost, t.getMessage(), t);

            SysOpLog op = new SysOpLog();
            op.setOrgId(1L);
            op.setUsername(username);
            op.setUri(uri);
            op.setHttpMethod(httpMethod);
            op.setClassMethod(pjp.getSignature().toShortString());
            op.setCostMs(cost);
            op.setSuccess(0);
            op.setErrorMsg(t.getMessage());
            opLogService.saveQuietly(op);

            throw t;
        }
    }
}
