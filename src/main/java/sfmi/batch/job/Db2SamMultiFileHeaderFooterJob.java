package sfmi.batch.job;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
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

public class Db2SamMultiFileHeaderFooterJob{
	
	public static final String JOB_NAME ="Db2SamMultiFileHeaderFooterJob";
	
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
				.writer(multiResourceItemWriter())
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
	 

	@Bean
	@StepScope
	public ItemProcessor<Pay,Pay> db2SamItemProcess() {
		log.info("Processor start!!!!");
		return item -> {
			log.info("Processor  >>>>>>>>>>>>>>>>>>>>>>>>>>>>> item:{}",item.toString());
			return item;
		};
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
		  writer.setLineAggregator(lineAggregator);
		  writer.setHeaderCallback(new FlatFileHeaderCallback() {
			
			@Override
			public void writeHeader(Writer writer) throws IOException {
				writer.write("########################################heaer#############################");
			}
		  });
		  writer.setFooterCallback(new FlatFileFooterCallback() {
			
			@Override
			public void writeFooter(Writer writer) throws IOException {
				writer.write("########################################footer#############################");
			}
		});
		
		 
		 
		  return writer;
	}
		
	public MultiResourceItemWriter<Pay> multiResourceItemWriter(){
		MultiResourceItemWriter<Pay> writer =  new MultiResourceItemWriter<>();
		writer.setResource(new FileSystemResource("target/pay_out"));
		writer.setDelegate(db2SamItemWriter());
		writer.setItemCountLimitPerResource(100);
		writer.setResourceSuffixCreator(new ResourceSuffixCreator() {
			@Override
			public String getSuffix(int index) {
				String suffix ="";
				suffix = "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + ".txt";
				return suffix;
			}
		});
		
		
		return writer;
		
	}
		
}
