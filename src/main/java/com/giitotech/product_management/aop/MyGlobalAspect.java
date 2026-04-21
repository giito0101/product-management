package com.giitotech.product_management.aop;

import java.util.logging.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component  // Bean として登録
public class MyGlobalAspect {
    private Logger logger = Logger.getLogger(getClass().getName());

    @Around("execution(* com.giitotech.product_management..*.*(..))") // 修正
    public Object aroundGetFortune(ProceedingJoinPoint theProceedingJoinPoint) throws Throwable {
        String method = theProceedingJoinPoint.getSignature().toShortString();
        logger.info("\n======>>> Executing @Around on method: " + method);

        long begin = System.currentTimeMillis();
        Object result = theProceedingJoinPoint.proceed();
        long end = System.currentTimeMillis();

        long duration = end - begin;
        logger.info("\n=====> Duration: " + duration / 1000.0 + " seconds");

        return result;
    }
}