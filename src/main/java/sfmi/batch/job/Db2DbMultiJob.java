package sfmi.batch.job;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;
import sfmi.batch.dto.Player;
import sfmi.batch.util.Incrementer;

@Configuration
@RequiredArgsConstructor
@Slf4j

public class Db2DbMultiJob{
	
	public static final String JOB_NAME ="Db2DbMultiJob";
	
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
		executor.setThreadNamePrefix("multi-thread-");
		executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
		executor.initialize();
				
		return executor;
	}	
	@Bean(name = JOB_NAME)
	public Job db2DbMultiJob() throws Exception {
		return jobBuilderFactory.get(JOB_NAME)
				.start(db2DbMultiStep())
				.incrementer(new Incrementer())
				.build();
	}
	@Bean(name = JOB_NAME + "_step")
	@JobScope
	public Step db2DbMultiStep() throws Exception {
		return stepBuilderFactory.get(JOB_NAME + "_step")
				.<Pay, Pay> chunk(chunkSize)
				.reader(db2DbMultiItemReader())
				.processor(db2DbMultiItemProcess())
				.writer(db2DbMultiItemWriter())
				.taskExecutor(executor())
                .throttleLimit(poolSize)
				.build();
	}

	
	  @Bean(name = JOB_NAME +"_reader")
	  
	  @StepScope 
	  public MyBatisPagingItemReader<Pay> db2DbMultiItemReader() throws Exception {
		  MyBatisPagingItemReader<Pay> myBatisPagingItemReader = new MyBatisPagingItemReader<Pay>();
		  myBatisPagingItemReader.setSqlSessionFactory(sqlSessionFactory);
		  myBatisPagingItemReader.setQueryId("sfmi.batch.dao.PayDAO.selectPaysByPaging");
		  
		  Map<String, Object> parameterValues = new HashMap<>();
		  parameterValues.put("amount", 2000);
		  myBatisPagingItemReader.setParameterValues(parameterValues);
		  myBatisPagingItemReader.setPageSize(chunkSize);
		  
		  return myBatisPagingItemReader;
	  
	  }

	@Bean
	@StepScope
	public ItemProcessor<Pay,Pay> db2DbMultiItemProcess() {
		return item -> {
			log.info("ItemProcessor  -----  item :{}", item);
			item.setTxName(item.getTxName()+"1");
			return item;
		};
	}	
	
	
	@Bean(name = JOB_NAME +"_writer")
	@StepScope
	public MyBatisBatchItemWriter<Pay> db2DbMultiItemWriter() throws Exception {
		  
		MyBatisBatchItemWriter<Pay> myBatchItemWriter = new MyBatisBatchItemWriter<Pay>();
		myBatchItemWriter.setSqlSessionFactory(sqlSessionFactory);
		myBatchItemWriter.setStatementId("sfmi.batch.dao.PayDAO.updatePay");
	  
		return myBatchItemWriter;
	  
	}
		
		
}
