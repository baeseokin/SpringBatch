package sfmi.batch.job;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;
import sfmi.batch.support.ColumnRangePartitioner;
import sfmi.batch.support.SfmiJobBuilderFactory;
import sfmi.batch.support.SfmiJobSupport;
import sfmi.batch.util.Incrementer;

@Configuration
@Slf4j
@ConditionalOnProperty(name="job.name", havingValue = "Db2DbPartitionHandlerJob")
public class Db2DbPartitionHandlerJob extends SfmiJobSupport{
	
	public Db2DbPartitionHandlerJob(SfmiJobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
		super(jobBuilderFactory, stepBuilderFactory, dataSource);
	}

	public final String JOB_NAME ="Db2DbPartitionHandlerJob";
		
	@Bean
	public ColumnRangePartitioner partitioner() {
		ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();
		columnRangePartitioner.setSqlSessionFactory(sqlSessionFactory);
		return columnRangePartitioner;
	}
	
	@Bean(name = JOB_NAME)
	public Job run() throws Exception {
		return jobBuilderFactory.get(JOB_NAME)
				.start(db2DbStep())
				.build();
	}
	@Bean(name = JOB_NAME + "step")
	public Step db2DbStep() throws Exception {
		return stepBuilderFactory.get(JOB_NAME + "_step")
				.partitioner(slaveStep().getName(), partitioner())
				.step(slaveStep())
				.gridSize(4)
				.taskExecutor(new SimpleAsyncTaskExecutor())
				.build();
	}
	
	@Bean
	public Step slaveStep() throws Exception {
		return stepBuilderFactory.get("slaveStep")
				.<Pay, Pay> chunk(chunkSize)
				.reader(db2DbItemReader(null, null))
				.writer(db2DbItemWriter())
				.build();
	}
	
	
	@Bean(name = JOB_NAME + "Reader")
	@StepScope 
	public MyBatisCursorItemReader<Pay> db2DbItemReader(
			@Value("#{stepExecutionContext[minValue]}") Long minValue,
			@Value("#{stepExecutionContext[maxValue]}") Long maxValue
			) throws Exception {
	  log.info("start db2DbItemReader!!!!");
	  log.info("minValue : {}", minValue);
	  log.info("maxValue : {}", maxValue);
  
	  Map<String, Object> parameterValues = new HashMap<String, Object>();
	  parameterValues.put("minValue", minValue);
	  parameterValues.put("maxValue", maxValue);
	  
	  MyBatisCursorItemReader<Pay> myBatisCursorItemReader = new MyBatisCursorItemReader<Pay>();
	  
	  myBatisCursorItemReader.setSqlSessionFactory(sqlSessionFactory);
	  myBatisCursorItemReader.setQueryId("sfmi.batch.dao.PayDAO.selectPayByPartition");
	  myBatisCursorItemReader.setParameterValues(parameterValues);
	  
	  return myBatisCursorItemReader;
  
	}


	public MyBatisBatchItemWriter<Pay> db2DbItemWriter() throws Exception {
		  
		MyBatisBatchItemWriter<Pay> myBatchItemWriter = new MyBatisBatchItemWriter<Pay>();
		myBatchItemWriter.setSqlSessionFactory(sqlSessionFactory);
		myBatchItemWriter.setStatementId("sfmi.batch.dao.PayDAO.updatePay");
	  
		return myBatchItemWriter;
	  
	}
		
		
}
