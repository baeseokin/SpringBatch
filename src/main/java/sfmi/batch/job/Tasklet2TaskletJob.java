package sfmi.batch.job;

import javax.sql.DataSource;

import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;
import sfmi.batch.support.SfmiJobBuilderFactory;
import sfmi.batch.support.SfmiJobSupport;
import sfmi.batch.tasklet.SimpleTasklet;
import sfmi.batch.tasklet.SimpleTasklet2;
import sfmi.batch.util.DataShareBean;
import sfmi.batch.util.Incrementer;

@Configuration
@Slf4j
@ConditionalOnProperty(name="job.name", havingValue = "Tasklet2TaskletJob")
public class Tasklet2TaskletJob extends SfmiJobSupport{
	
	public Tasklet2TaskletJob(SfmiJobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
		super(jobBuilderFactory, stepBuilderFactory, dataSource);
		// TODO Auto-generated constructor stub
	}

	public final String JOB_NAME ="Tasklet2TaskletJob";
	

	@Bean(name = JOB_NAME)
	public Job run() throws Exception {
		return jobBuilderFactory.get(JOB_NAME)
				.start(Tasklet2TaskletStep1())
				.next(Tasklet2TaskletStep2())
				.build();
	}
	
	@Bean(name = JOB_NAME + "_step1")
	@JobScope
	public Step Tasklet2TaskletStep1() throws Exception {
		
		return stepBuilderFactory.get(JOB_NAME + "_step1")
				.tasklet(new SimpleTasklet())
				.build();
	}
	
	@Bean(name = JOB_NAME + "_step2")
	@JobScope
	public Step Tasklet2TaskletStep2() throws Exception {
		
		return stepBuilderFactory.get(JOB_NAME + "_step2")
				.tasklet(new SimpleTasklet2())
				.build();
	}		
		
		
}
