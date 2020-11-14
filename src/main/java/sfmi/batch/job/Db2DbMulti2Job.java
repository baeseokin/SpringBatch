package sfmi.batch.job;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;
import sfmi.batch.util.Incrementer;

@Configuration
@RequiredArgsConstructor
@Slf4j

public class Db2DbMulti2Job{
	
	public static final String JOB_NAME ="Db2DbMulti2Job";
	
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource;
	
	@Autowired 
	private SqlSessionFactory sqlSessionFactory;

	private int chunkSize = 10;
	
	@Value("${chunkSize:10}")
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	
	private int poolSize;
	
	@Value("${poolSize:10}")
	public void setPoolSizee(int poolSize) {
		this.poolSize = poolSize;
	}	
	
	@Bean(name = JOB_NAME + "taskPool")
	public TaskExecutor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(poolSize);
		executor.setMaxPoolSize(poolSize);
		executor.setThreadNamePrefix("Multi2-thread-");
		executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
		executor.initialize();
				
		return executor;
	}	
	@Bean(name = JOB_NAME)
	public Job db2DbMulti2Job() throws Exception {
		return jobBuilderFactory.get(JOB_NAME)
				.start(db2DbMulti2Step())
				.incrementer(new Incrementer())
				.build();
	}
	@Bean(name = JOB_NAME + "_step")
	@JobScope
	public Step db2DbMulti2Step() throws Exception {
		return stepBuilderFactory.get(JOB_NAME + "_step")
				.<Pay, Pay> chunk(chunkSize)
				.reader(db2DbMulti2ItemReader())
				.processor(db2DbMulti2ItemProcess())
				.writer(db2DbMulti2ItemWriter())
				.taskExecutor(executor())
                .throttleLimit(poolSize)
				.build();
	}

	
	  @Bean(name = JOB_NAME +"_reader")
	  @StepScope 
	  public SynchronizedItemStreamReader<Pay> db2DbMulti2ItemReader() throws Exception {
		  MyBatisCursorItemReader<Pay> myBatisCursorItemReader = new MyBatisCursorItemReader<Pay>();
		  
		  myBatisCursorItemReader.setSqlSessionFactory(sqlSessionFactory);
		  myBatisCursorItemReader.setQueryId("sfmi.batch.dao.PayDAO.selectPays");
		  
		  SynchronizedItemStreamReader<Pay> synchronizedItemStreamReader = new SynchronizedItemStreamReader<Pay>();
		  synchronizedItemStreamReader.setDelegate(myBatisCursorItemReader);
		  return synchronizedItemStreamReader;
	  }

	@Bean
	@StepScope
	public ItemProcessor<Pay,Pay> db2DbMulti2ItemProcess() {
		return item -> {
			log.info("ItemProcessor  -----  item :{}", item);
			item.setTxName(item.getTxName()+"1");
			return item;
		};
	}	
	
	
	@Bean(name = JOB_NAME +"_writer")
	@StepScope
	public MyBatisBatchItemWriter<Pay> db2DbMulti2ItemWriter() throws Exception {
		  
		MyBatisBatchItemWriter<Pay> myBatchItemWriter = new MyBatisBatchItemWriter<Pay>();
		myBatchItemWriter.setSqlSessionFactory(sqlSessionFactory);
		myBatchItemWriter.setStatementId("sfmi.batch.dao.PayDAO.updatePay");
	  
		return myBatchItemWriter;
	  
	}
		
		
}
