package sfmi.batch.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.stereotype.Component;

import sfmi.batch.retry.NexusRetryTemplate;


@Aspect
@Component
public class NexusRetryTemplateAspect {
	
	@Autowired
	private NexusRetryTemplate retryTemplate;

	@Pointcut(value = "execution(* org.springframework.batch.item.ItemProcessor..*(..))")
	public void processorPointCut() {}
	
	@Pointcut(value = "execution(* org.mybatis.spring.batch.MyBatisBatchItemWriter..*(..))")
	public void writerPointCut() {}	
	
	
	//@Around("execution(* org.springframework.batch.item.ItemProcessor..*(..))")
	@Around("processorPointCut() || writerPointCut()")
    public Object doRetry(ProceedingJoinPoint joinPoint) throws Throwable{
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>AOP -- doRetry"); // Obtaining method
		
		Object proceed = null;
		
	    try {
			return retryTemplate.retryTemplate().execute(
					new RetryCallback() {
						@Override
						public Object doWithRetry(RetryContext context) throws Throwable {
							System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>AOP -- doWithRetry");
							return joinPoint.proceed(); 
						}
						
					}
					);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return joinPoint.proceed(); 
	}

}
