package sfmi.batch.job;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class MultiThreadJdbcPagingItemReaderJobConfiguration {
	public static final String JOB_NAME ="MultiThreadJdbcPagingItemReaderJob";

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource;
	
	private int chunkSize;
	
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
	public Job jdbcPagingItemReaderJob() throws Exception {
		return jobBuilderFactory.get(JOB_NAME)
				.start(jdbcPagingItemReaderStep())
				.build();
	}
	
	@Bean(name = JOB_NAME + "_step")
	@JobScope
	public Step jdbcPagingItemReaderStep() throws Exception{
		return stepBuilderFactory.get(JOB_NAME + "_step")
				.<Pay, Pay> chunk(chunkSize)
				.reader(jdbcPagingItemReader(null))
				.writer(jdbcPagingItemWriter())
				.taskExecutor(executor())
                .throttleLimit(poolSize)
				.build();
	}

	@Bean(name = JOB_NAME +"_reader")
	@StepScope
	public JdbcPagingItemReader<Pay> jdbcPagingItemReader(@Value("#{jobParameters[createDate]}") String createDate) throws Exception {
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("amount", 2000);
		
		log.info("jdbcPagingItemReader --- createDate :{}", createDate );	
		
		return new JdbcPagingItemReaderBuilder<Pay>()
				.pageSize(chunkSize)
				.dataSource(dataSource)
				.rowMapper(new BeanPropertyRowMapper<>(Pay.class))
				.queryProvider(createQueryProvider())
				.parameterValues(parameterValues)
				.saveState(false)
				.name(JOB_NAME +"_reader")
				.build();
				
	}
	@Bean(name = JOB_NAME +"_writer")
    @StepScope
	public ItemWriter<Pay> jdbcPagingItemWriter() {
		return list -> {
			for(Pay pay : list) {
				log.info("Current Pay={}", pay);
			}
		};
	}	
	@Bean
	public PagingQueryProvider createQueryProvider() throws Exception{
		SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
		queryProvider.setDataSource(dataSource);
		queryProvider.setSelectClause("id, amount, tx_name, tx_date_time");
		queryProvider.setFromClause("from pay");
		queryProvider.setWhereClause("where amount >= :amount");
		
		Map<String, Order> sortKeys = new HashMap<>(1);
		sortKeys.put("id", Order.ASCENDING);
		
		queryProvider.setSortKeys(sortKeys);
		
		return queryProvider.getObject();
	}




}
