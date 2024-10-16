package com.example.springboot;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@SuppressWarnings({"nls", "static-method", "boxing"})
public class LoggingAspect {

    // Pointcut for all methods in ItemController
    @Pointcut("execution(* com.example.springboot.HelloController.*(..))")
    public void controllerMethods() {}

    // Before advice: logs before method execution
	@Before("controllerMethods()")
    public void logBeforeExecution(JoinPoint joinPoint) {
        log.info("Starting execution of: {}", joinPoint.getSignature());
    }

    // After advice: logs after method execution
    @After("controllerMethods()")
    public void logAfterExecution(JoinPoint joinPoint) {
        log.info("Finished execution of: {}", joinPoint.getSignature());
    }

    // Around advice: logs execution time of method
    @Around("controllerMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        log.info("{} executed in {} ms", joinPoint.getSignature(), executionTime);
        return proceed;
    }
}