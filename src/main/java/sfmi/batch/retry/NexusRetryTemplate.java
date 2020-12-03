package sfmi.batch.retry;

import java.sql.BatchUpdateException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class NexusRetryTemplate {
	
	@Value("${retry.max-attempts:10}")
	private int maxAttempts;
	@Value("${retry.delay:10}")
	private long delay;

	@Bean
	public RetryTemplate retryTemplate() {
		Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<Class<? extends Throwable>, Boolean>();

		retryableExceptions.put(RuntimeException.class, true);
		retryableExceptions.put(BatchUpdateException.class, true);
		
		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxAttempts, retryableExceptions);
        

       FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
       backOffPolicy.setBackOffPeriod(delay);

       RetryTemplate template = new RetryTemplate();
       template.setRetryPolicy(retryPolicy);
       template.setBackOffPolicy(backOffPolicy);
       
       return template;
	}

}
