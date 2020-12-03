package sfmi.batch.job;

import java.sql.BatchUpdateException;

import javax.annotation.PostConstruct;

import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.aop.MyContextContainer;
import sfmi.batch.aop.NexusDB;
import sfmi.batch.dto.Pay;
import sfmi.batch.support.SfmiJobSupport;
import sfmi.batch.support.DaoInitializeService;
import sfmi.batch.util.Incrementer;

@Configuration
@Slf4j
@ConditionalOnProperty(name="job.name", havingValue = Db2DbJob.JOB_NAME)
public class Db2DbJob extends SfmiJobSupport{

	public static final String JOB_NAME ="Db2DbJob";
	public String dbName = "fw";
	
	@Autowired
	public DaoInitializeService testService;
	
	@Bean(name = JOB_NAME)
	public Job run(Step db2DbStep) throws Exception {

		return jobBuilderFactory.get(JOB_NAME)
				.start(db2DbStep)
				.incrementer(new Incrementer())
				.build();
	}
	@Bean(name = JOB_NAME + "step")
	@JobScope
	public Step db2DbStep(MyBatisCursorItemReader<Pay> db2DbItemReader, ItemProcessor<Pay,Pay> db2DbItemProcess, MyBatisBatchItemWriter<Pay> db2DbItemWriter ) throws Exception {
		return stepBuilderFactory.get(JOB_NAME + "_step")
				.<Pay, Pay> chunk(chunkSize)
				.faultTolerant() 
				.skipLimit(5) 
				.skip(RuntimeException.class)
				.skip(BatchUpdateException.class)
				.reader(db2DbItemReader)
				.processor(db2DbItemProcess)
				.writer(db2DbItemWriter)
				.build();
	}
	@Bean
	@StepScope 
	public MyBatisCursorItemReader<Pay> db2DbItemReader(@Value("#{jobParameters[createDate]}") String createDate) throws Exception {
	  log.info("start MyBatisCursorItemReader!!!!");
	  log.info("createDate : {}", createDate);
  
	  MyBatisCursorItemReader<Pay> myBatisCursorItemReader = new MyBatisCursorItemReader<Pay>();
	  
	  myBatisCursorItemReader.setSqlSessionFactory(sqlSessionFactory);
	  myBatisCursorItemReader.setQueryId("sfmi.batch.dao.PayDAO.selectPays");
	  
	  return myBatisCursorItemReader;
  
	}
	@Bean
	public ItemProcessor<Pay,Pay> db2DbItemProcess() {
		return item -> {
			log.info("ItemProcessor  -----  item :{}", item);
			
			
			
			if(item.getAmount() < 10000) {
				item.setTxName(item.getTxName()+"1");
				return item;
			}if(item.getId() == 204){
				item.setTxName("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
				return item;
				//throw new RuntimeException();
			}else {
				return item;
			}
		};
	}	
	@Bean
	public MyBatisBatchItemWriter<Pay> db2DbItemWriter() throws Exception {
		  
		MyBatisBatchItemWriter<Pay> myBatchItemWriter = new MyBatisBatchItemWriter<Pay>();
		myBatchItemWriter.setSqlSessionFactory(sqlSessionFactory);
		myBatchItemWriter.setStatementId("sfmi.batch.dao.PayDAO.updatePay");
	  
		return myBatchItemWriter;
	  
	}
	
	@PostConstruct
	public void dbInitialize() {
		log.info("loadDbProperties  -----  dbName :{}", dbName);
		this.loadDbProperties(dbName);
		
		
		/*
		 * MyContextContainer container = new MyContextContainer(); try {
		 * log.info("this. Class :{}", this.getClass()); container.get(this.getClass());
		 * } catch (IllegalAccessException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (InstantiationException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}
		
		
}
