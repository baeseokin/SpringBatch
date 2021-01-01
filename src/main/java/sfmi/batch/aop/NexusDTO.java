package sfmi.batch.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,          // 메소드 선언시
    ElementType.PARAMETER,
    ElementType.FIELD
    })
public @interface NexusDTO {
	String type() default "String";
	String align() default "left";
	int size();
}
