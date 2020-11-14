package sfmi.batch.util;

import java.util.Random;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OddDecider implements JobExecutionDecider{

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

		Random rand = new Random();
		int randomNumber = rand.nextInt(50) +1;
		log.info("랜덤숫자: {}", randomNumber);
		
		if(randomNumber %2 == 0) {
			return new FlowExecutionStatus("EVEN");
		}else {
			return new FlowExecutionStatus("ODD");
		}
	}

}
