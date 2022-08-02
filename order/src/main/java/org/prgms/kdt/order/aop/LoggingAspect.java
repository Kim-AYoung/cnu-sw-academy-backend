package org.prgms.kdt.order.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(org.prgms.kdt.order.aop.TrackTime)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Before method called. {}", joinPoint.getSignature().toString());
        var startTime = System.nanoTime();
        var result = joinPoint.proceed();
        var intervalTime = (System.nanoTime() - startTime);
        log.info("After method called with result => {} and time taken {} nanoseconds", result, intervalTime);
        return result;
    }
}
