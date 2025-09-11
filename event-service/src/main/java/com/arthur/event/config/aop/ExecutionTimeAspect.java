package com.arthur.event.config.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {

    @Around("within(com.arthur.event.application..*)")
    public Object logServiceExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            long ms = (System.nanoTime() - start) / 1_000_000;
            String method = pjp.getSignature().toShortString();

            if(ms >= 500) {
                log.warn("SLOW {} took {} ms", method, ms);
            } else {
                log.info("{} took {} ms", method, ms);
            }
        }
    }
}
