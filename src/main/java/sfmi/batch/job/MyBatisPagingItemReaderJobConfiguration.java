package sfmi.batch.job;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class MyBatisPagingItemReaderJobConfiguration {
	public static final String JOB_NAME ="myBatisPagingItemReaderJob";

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource;
	
	private int chunkSize;
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Value("${chunkSize:10}")
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	

	
	@Bean(name = JOB_NAME)
	public Job myBatisPagingItemReaderJob() throws Exception {
		return jobBuilderFactory.get(JOB_NAME)
				.start(myBatisPagingItemReaderStep())
				.build();
	}
	
	@Bean(name = JOB_NAME + "_step")
	@JobScope
	public Step myBatisPagingItemReaderStep() throws Exception{
		return stepBuilderFactory.get(JOB_NAME + "_step")
				.<Pay, Pay> chunk(chunkSize)
				.reader(myBatisPagingItemReader(null))
				.writer(myBatisPagingItemWriter())
				.build();
	}

	@Bean(name = JOB_NAME +"_reader")
	@StepScope
	public MyBatisPagingItemReader<Pay> myBatisPagingItemReader(@Value("#{jobParameters[createDate]}") String createDate) throws Exception {
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("amount", 2000);
		
		log.info("myBatisPagingItemReader --- createDate :{}", createDate );	
		
		return new MyBatisPagingItemReaderBuilder<Pay>()
				.sqlSessionFactory(sqlSessionFactory)	
				.queryId("sfmi.batch.dao.PayDAO.selectPaysByPaging")
				.parameterValues(parameterValues)
				.pageSize(chunkSize)
				.build();
				
				
	}
	@Bean(name = JOB_NAME +"_writer")
    @StepScope
	public ItemWriter<Pay> myBatisPagingItemWriter() {
		return list -> {
			for(Pay pay : list) {
				log.info("myBatisPagingItemWriter-----Current Pay={}", pay);
			}
		};
	}	





}
