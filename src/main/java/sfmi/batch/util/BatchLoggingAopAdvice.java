package sfmi.batch.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class BatchLoggingAopAdvice {
	
	@Around("@annotation(sfmi.batch.util.BatchLogging)")
	public Object processBatchLogging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
		BatchLogging batchLogging = methodSignature.getMethod().getAnnotation(BatchLogging.class);
        log.info("execute custom annotation processing with annotation param = {}, {}", batchLogging.sqlId());
        log.info("Before invoke getSomeValue()");
        Object proceedReturnValue = proceedingJoinPoint.proceed();
        log.info("After invoke getSomeValue(), result:{}", proceedReturnValue);
        return proceedReturnValue;
	}

}
