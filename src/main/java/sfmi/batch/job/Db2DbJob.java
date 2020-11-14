package sfmi.batch.job;

import javax.sql.DataSource;

import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;
import sfmi.batch.support.SfmiJobBuilderFactory;
import sfmi.batch.support.SfmiJobSupport;
import sfmi.batch.util.Incrementer;

@Configuration
@Slf4j
@ConditionalOnProperty(name="job.name", havingValue = "Db2DbJob")
public class Db2DbJob extends SfmiJobSupport{
	
	public Db2DbJob(SfmiJobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
		super(jobBuilderFactory, stepBuilderFactory, dataSource);
		// TODO Auto-generated constructor stub
	}

	public final String JOB_NAME ="Db2DbJob";
	
	@Bean(name = JOB_NAME)
	public Job run() throws Exception {
		return jobBuilderFactory.get(JOB_NAME)
				.start(db2DbStep(null))
				.incrementer(new Incrementer())
				.build();
	}
	@Bean(name = JOB_NAME + "step")
	@JobScope
	public Step db2DbStep(@Value("#{jobParameters[createDate]}") String createDate) throws Exception {
		log.info("db2DbStep ---- createDate : {}", createDate);
		return stepBuilderFactory.get(JOB_NAME + "_step")
				.<Pay, Pay> chunk(chunkSize)
				.reader(db2DbItemReader(null))
				.processor(db2DbItemProcess())
				.writer(db2DbItemWriter())
				.build();
	}
	@Bean(name = JOB_NAME + "Reader")
	@StepScope 
	public MyBatisCursorItemReader<Pay> db2DbItemReader(@Value("#{jobParameters[createDate]}") String createDate) throws Exception {
	  log.info("start MyBatisCursorItemReader!!!!");
	  log.info("createDate : {}", createDate);
  
	  MyBatisCursorItemReader<Pay> myBatisCursorItemReader = new MyBatisCursorItemReader<Pay>();
	  
	  myBatisCursorItemReader.setSqlSessionFactory(sqlSessionFactory);
	  myBatisCursorItemReader.setQueryId("sfmi.batch.dao.PayDAO.selectPays");
	  
	  return myBatisCursorItemReader;
  
	}

	public ItemProcessor<Pay,Pay> db2DbItemProcess() {
		return item -> {
			log.info("ItemProcessor  -----  item :{}", item);
			item.setTxName(item.getTxName()+"1");
			return item;
		};
	}	
	
	public MyBatisBatchItemWriter<Pay> db2DbItemWriter() throws Exception {
		  
		MyBatisBatchItemWriter<Pay> myBatchItemWriter = new MyBatisBatchItemWriter<Pay>();
		myBatchItemWriter.setSqlSessionFactory(sqlSessionFactory);
		myBatchItemWriter.setStatementId("sfmi.batch.dao.PayDAO.updatePay");
	  
		return myBatchItemWriter;
	  
	}
		
		
}
