package sfmi.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleStepListener implements StepExecutionListener{@Override
	public void beforeStep(StepExecution stepExecution) {
		log.info("###################### Before Step Start");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		log.info("###################### After Step End");
		return ExitStatus.COMPLETED;
	}


}
