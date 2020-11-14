package sfmi.batch.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.support.SfmiJobBuilderFactory;
import sfmi.batch.support.SfmiJobSupport;
import sfmi.batch.tasklet.SimpleTasklet;
import sfmi.batch.util.OddDecider;

@Configuration
@Slf4j
@ConditionalOnProperty(name="job.name", havingValue = "StepFlowParallelJob")
public class StepFlowParallelJob extends SfmiJobSupport{

	public StepFlowParallelJob(SfmiJobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
			DataSource dataSource) {
		super(jobBuilderFactory, stepBuilderFactory, dataSource);
		// TODO Auto-generated constructor stub
	}

	public final String JOB_NAME ="StepFlowParallelJob";
	
	@Bean
	public JobExecutionDecider decider() {
		return new OddDecider();
	}
	
	@Bean(name = JOB_NAME)
	public Job run() throws Exception {
		
		Flow flow1 = new FlowBuilder<Flow>("flow1")
				.start(step1())
				.next(step2())
				.build();
		Flow flow2 = new FlowBuilder<Flow>("flow2")
				.start(step3())
				.build();
		
		Flow parallelStepFlow = new FlowBuilder<Flow>("parallelStepFlow")
				.split(new SimpleAsyncTaskExecutor())
				.add(flow1, flow2)
				.build();
				
		return jobBuilderFactory.get(JOB_NAME)
		.start(parallelStepFlow)
		.next(step4())
		.build()
		.build();
	}

	@Bean(name = JOB_NAME + "step1")
	public Step step1() {
		return stepBuilderFactory.get(JOB_NAME + "step1")
				.tasklet(Step1Tasklet())
				.build();
	}

	public Tasklet Step1Tasklet() {
		log.info("Step1Tasklet start !!!!!!!!");
		return new SimpleTasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

				log.info("Step1Tasklet execute  ----------------->");
				
				/**
                	ExitStatus를 FAILED로 지정한다.
                	해당 status를 보고 flow가 진행된다.
                **/
				//contribution.setExitStatus(ExitStatus.FAILED);
				
				return  RepeatStatus.FINISHED;
			}
		};
	}
	
	@Bean(name = JOB_NAME + "step2")
	public Step step2() {
		return stepBuilderFactory.get(JOB_NAME + "step2")
				.tasklet(Step2Tasklet())
				.build();
	}

	public Tasklet Step2Tasklet() {
		log.info("Step2Tasklet start !!!!!!!!");
		return new SimpleTasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

				log.info("Step2Tasklet execute  ----------------->");
				
				return  RepeatStatus.FINISHED;
			}
		};
	}	
	
	@Bean(name = JOB_NAME + "step3")
	public Step step3() {
		return stepBuilderFactory.get(JOB_NAME + "step3")
				.tasklet(Step3Tasklet())
				.build();
	}

	public Tasklet Step3Tasklet() {
		log.info("Step3Tasklet start !!!!!!!!");
		return new SimpleTasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

				log.info("Step3Tasklet execute  ----------------->");
				
				return  RepeatStatus.FINISHED;
			}
		};
	}
	
	@Bean(name = JOB_NAME + "step4")
	public Step step4() {
		return stepBuilderFactory.get(JOB_NAME + "step4")
				.tasklet(Step4Tasklet())
				.build();
	}

	public Tasklet Step4Tasklet() {
		log.info("Step4Tasklet start !!!!!!!!");
		return new SimpleTasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

				log.info("Step4Tasklet execute  ----------------->");
				
				return  RepeatStatus.FINISHED;
			}
		};
	}	
}
