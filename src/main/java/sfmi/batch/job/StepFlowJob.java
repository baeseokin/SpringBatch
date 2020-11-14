package sfmi.batch.job;

import javax.sql.DataSource;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.support.SfmiJobBuilderFactory;
import sfmi.batch.support.SfmiJobSupport;
import sfmi.batch.tasklet.SimpleTasklet;

@Configuration
@Slf4j
@ConditionalOnProperty(name="job.name", havingValue = "StepFlowJob")
public class StepFlowJob extends SfmiJobSupport{

	public StepFlowJob(SfmiJobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
			DataSource dataSource) {
		super(jobBuilderFactory, stepBuilderFactory, dataSource);
		// TODO Auto-generated constructor stub
	}

	public final String JOB_NAME ="StepFlowJob";
	
	@Bean(name = JOB_NAME)
	public Job run() throws Exception {
		return jobBuilderFactory.get(JOB_NAME)
		.start(step1())
			.on(ExitStatus.FAILED.getExitCode())
				.to(step3())
					.on("*")
					.end()
			.from(step1())
				.on("*")
					.to(step2())
						.on("*")
						.end()
			.end()		
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
				contribution.setExitStatus(ExitStatus.FAILED);
				
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
}
