package sfmi.batch.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class NexusDBAspect {
	
	  @Pointcut("@annotation(sfmi.batch.aop.NexusDB)") 
	  public void loadDbInitialize(){}
	  
	  @Before(value = "loadDbInitialize()") 
	  public void beforeAdvice(JoinPoint joinPoint) {
		    System.out.println("loadDbInitialize start!!!");
		    
		    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		    System.out.println("Method :: " + methodSignature.getName());
		    if("loadDbProperties".equals(methodSignature.getName())) {
		    	for (Object arg: joinPoint.getArgs()) {
			    	if(arg != null) {
			    		String dbName = (String)arg;
			    		System.out.println("db Loding start ~!!!!!!!  dbName : " + dbName);
			    	}
			        
			    }
		    }    
		    System.out.println("-- loadDbInitialize end!!!");
	}
	 
	
	@Before(value = "execution(* run(org.springframework.batch.core.Step))")
    public void beforeStep(){
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>beforeStep"); // Obtaining method
	}
	/*
	 * @Around("@annotation(sfmi.batch.aop.NexusDB)") public Object
	 * setDB(ProceedingJoinPoint joinPoint) throws Throwable{ Object[] args =
	 * joinPoint.getArgs();
	 * 
	 * System.out.println("setDB - joinPoint.getArgs : "+ args); // Obtaining method
	 * parameters MethodSignature signature = (MethodSignature)
	 * joinPoint.getSignature(); Parameter[] parameters =
	 * signature.getMethod().getParameters(); for (int i = 0; i < parameters.length;
	 * i++) { Parameter parameter = parameters[i]; // How Java handles its own basic
	 * types of parameters (such as Integer, String)
	 * 
	 * NexusDB nexusDb = parameter.getAnnotation(NexusDB.class); if (nexusDb != null
	 * && args[i] == null) { throw new RuntimeException(parameter.toString() +
	 * nexusDb.message()); }else { System.out.println("setDB - argument : "+
	 * args[i]); } //TODO continue;
	 * 
	 * } Object proceed = joinPoint.proceed(); return proceed;
	 * 
	 * }
	 */
}
