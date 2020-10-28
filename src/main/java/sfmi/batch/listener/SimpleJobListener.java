package sfmi.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleJobListener implements JobExecutionListener{

	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info(">>>>>>>>>>>>>>>>>>>>>>>Before Job Start");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		log.info(">>>>>>>>>>>>>>>>>>>>>>>After Job End");
	}
	
}
