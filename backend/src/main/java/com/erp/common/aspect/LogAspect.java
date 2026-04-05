package com.erp.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Around("execution(* com.erp..web..*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object ret = pjp.proceed();
            long cost = System.currentTimeMillis() - start;
            log.info("{} cost={}ms", pjp.getSignature(), cost);
            return ret;
        } catch (Throwable t) {
            long cost = System.currentTimeMillis() - start;
            log.error("{} failed cost={}ms msg={}", pjp.getSignature(), cost, t.getMessage(), t);
            throw t;
        }
    }
}
