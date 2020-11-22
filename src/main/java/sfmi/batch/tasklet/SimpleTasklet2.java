package sfmi.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleTasklet2 implements Tasklet{

	private StepExecution stepExecution;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info("SimpleTasklet2  ----------------->");
		ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		
		String paramData = jobExecutionContext.getString("paramData");
		
		log.info("SimpleTasklet2 , paramData:{}", paramData);
		
		return  RepeatStatus.FINISHED;
	}
	

}
