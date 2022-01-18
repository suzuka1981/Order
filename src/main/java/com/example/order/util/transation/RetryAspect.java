package com.example.order.util.transation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Aspect
@Component
public class RetryAspect {

    @Pointcut("@annotation(com.example.order.util.transation.Retry)")
    public void retryPointcut() {

    }

    @Around("retryPointcut() && @annotation(retry)")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Object tryAgain(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        int count = 0;
        do {
            count++;
            try {
                return joinPoint.proceed();
            } catch (RetryException e) {
                if (count > retry.value()) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return "error";
                }
            }
        } while (true);
    }
}