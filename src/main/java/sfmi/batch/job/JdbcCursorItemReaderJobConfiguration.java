package sfmi.batch.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;
import sfmi.batch.listener.SimpleJobListener;
import sfmi.batch.listener.SimpleStepItemReadListener;
import sfmi.batch.listener.SimpleStepListener;
import sfmi.batch.util.BatchLogging;
import sfmi.batch.util.Incrementer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JdbcCursorItemReaderJobConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource;
	
	private static final int chunkSize = 10;
	
	@Bean
	public Job jdbcCursorItemReaderJob() {
		return jobBuilderFactory.get("jdbcCursorItemReaderJob")
				.start(jdbcCursorItemReaderStep())
				.incrementer(new Incrementer())
				.build();
	}
	@Bean
	public Step jdbcCursorItemReaderStep() {
		return stepBuilderFactory.get("jdbcCursorItemReaderStep")
				.listener(new SimpleStepItemReadListener())
				.<Pay, Pay>chunk(chunkSize)
				.reader(jdbcCursorItemReader())
				.writer(jdbcCursorItemWriter())
				.build();
	}
	@Bean
	public JdbcCursorItemReader<Pay> jdbcCursorItemReader() {
		return new JdbcCursorItemReaderBuilder<Pay>()
				.fetchSize(chunkSize)
				.dataSource(dataSource)
				.rowMapper(new BeanPropertyRowMapper<>(Pay.class))
				.sql("SELECT id, amount, tx_name, tx_date_time FROM pay")
				.name("jdbcCursorItemReader")
				.build();
	}
	@Bean
	@BatchLogging(sqlId="firstSqlId")
	public ItemWriter<Pay> jdbcCursorItemWriter() {
		return list -> {
			for(Pay pay:list) {
				log.info("Current Pay = {}", pay);
			}
		};
	}
	
}


