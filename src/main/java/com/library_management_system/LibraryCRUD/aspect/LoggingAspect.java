package com.library_management_system.LibraryCRUD.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);


    @Pointcut("execution(* com.library_management_system.LibraryCRUD.service.BookService.*(..)) || " +
            "execution(* com.library_management_system.LibraryCRUD.service.PatronService.*(..)) || " +
            "execution(* com.library_management_system.LibraryCRUD.service.BorrowingService.*(..)) "
    )
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        // Extract the method name
        String methodName = joinPoint.getSignature().getName();

        // Log the entry of the method
        logger.info("Entering method: {}", methodName);

        long start = System.currentTimeMillis();

        Object result = null;
        try {
            // Proceed with the method execution
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            // Log the exception
            logger.error("Exception in method: {} with message: {}",
                    methodName,
                    throwable.getMessage());
            throw throwable;
        }

        long executionTime = System.currentTimeMillis() - start;

        // Log the method execution time
        logger.info("Method {} executed in {} ms", methodName, executionTime);

        return result;
    }



}
