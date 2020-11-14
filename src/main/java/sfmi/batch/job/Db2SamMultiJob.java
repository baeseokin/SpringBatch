package sfmi.batch.job;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;
import sfmi.batch.dto.Player;
import sfmi.batch.util.Incrementer;

@Configuration
@RequiredArgsConstructor
@Slf4j

public class Db2SamMultiJob{
	
	public static final String JOB_NAME ="Db2SamMultiJob";
	
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
	
	
	@Bean(name = JOB_NAME)
	public Job db2SamJob() throws Exception {
		return jobBuilderFactory.get(JOB_NAME)
				.start(db2SamStep())
				.incrementer(new Incrementer())
				.build();
	}
	@Bean(name = JOB_NAME + "_step")
	@JobScope
	public Step db2SamStep() throws Exception {
		return stepBuilderFactory.get(JOB_NAME + "_step")
				.<Pay, Pay> chunk(chunkSize)
				.reader(db2SamItemReader())
				.processor(db2SamItemProcess())
				.writer(db2SamMultiItemWriter())
				.build();
	}

	
	  @Bean(name = JOB_NAME +"_reader")
	  
	  @StepScope 
	  public MyBatisCursorItemReader<Pay> db2SamItemReader() throws Exception {
		  log.info("start MyBatisCursorItemReader!!!!");
	  
		  MyBatisCursorItemReader<Pay> myBatisCursorItemReader = new MyBatisCursorItemReader<Pay>();
		  
		  myBatisCursorItemReader.setSqlSessionFactory(sqlSessionFactory);
		  myBatisCursorItemReader.setQueryId("sfmi.batch.dao.PayDAO.selectPays");
		  
		  return myBatisCursorItemReader;
	  
	  }
	 
	/*
	 * @Bean(name = JOB_NAME +"_reader")
	 * 
	 * @StepScope public JdbcCursorItemReader<Pay> db2SamItemReader() throws
	 * Exception{
	 * 
	 * JdbcCursorItemReader<Pay> reader = new JdbcCursorItemReader<Pay>();
	 * reader.setFetchSize(chunkSize); reader.setDataSource(dataSource);
	 * reader.setRowMapper(new BeanPropertyRowMapper<>(Pay.class));
	 * reader.setSql("SELECT id, amount, tx_name, tx_date_time FROM pay");
	 * reader.setName(JOB_NAME +"_reader"); return reader; }
	 */
	
	@Bean
	@StepScope
	public ItemProcessor<Pay,Pay> db2SamItemProcess() {
		log.info("Processor start!!!!");
		return item -> {
			log.info("Processor  >>>>>>>>>>>>>>>>>>>>>>>>>>>>> item:{}",item.toString());
			return item;
		};
	}	
	
	
	
	
	
	@Bean(name = JOB_NAME +"_MultiWriter")
	@StepScope
	public CompositeItemWriter<Pay> db2SamMultiItemWriter(){
		List<ItemWriter> writers = new ArrayList<>(2);
		writers.add(db2SamItemWriter());
		writers.add(db2SamItemWriter2());
		
		CompositeItemWriter compositeItemWriter = new CompositeItemWriter();
		compositeItemWriter.setDelegates(writers);
		
		return compositeItemWriter;
	}
		
	@Bean(name = JOB_NAME +"_writer")
	@StepScope
	public FlatFileItemWriter<Pay> db2SamItemWriter(){
		
		  BeanWrapperFieldExtractor<Player> fieldExtractor = new  BeanWrapperFieldExtractor<>(); 
		  fieldExtractor.setNames(new String[]{"id", "amount", "txName"});
		  fieldExtractor.afterPropertiesSet();
		  
		  DelimitedLineAggregator<Player> lineAggregator = new DelimitedLineAggregator<>(); 
		  lineAggregator.setDelimiter(",");
		  lineAggregator.setFieldExtractor(fieldExtractor);
		  
		  FlatFileItemWriter writer = new FlatFileItemWriter();
		  writer.setResource(new FileSystemResource("target/pay_out2.txt"));
		  writer.setLineAggregator(lineAggregator);
	
		  return writer;
	}

	@Bean(name = JOB_NAME +"_writer2")
	@StepScope
	public FlatFileItemWriter<Pay> db2SamItemWriter2(){
		
		  BeanWrapperFieldExtractor<Player> fieldExtractor = new  BeanWrapperFieldExtractor<>(); 
		  fieldExtractor.setNames(new String[]{"id", "amount", "txName"});
		  fieldExtractor.afterPropertiesSet();
		  
		  DelimitedLineAggregator<Player> lineAggregator = new DelimitedLineAggregator<>(); 
		  lineAggregator.setDelimiter("|");
		  lineAggregator.setFieldExtractor(fieldExtractor);
		  
		  FlatFileItemWriter writer = new FlatFileItemWriter();
		  writer.setResource(new FileSystemResource("target/pay_out3.txt"));
		  writer.setLineAggregator(lineAggregator);
	
		  return writer;
	}	
}
