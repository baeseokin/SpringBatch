package sfmi.batch.job;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;
import sfmi.batch.util.CreateDateJobParameter;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MyBatisCursorItemReaderJobConfiguration {
	
	public static final String JOB_NAME ="MyBatisCursorItemReaderJob";
	
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource;
	private final CreateDateJobParameter jobParameter;
	
	private static final int chunkSize = 10;
	
	@Bean
	@JobScope
	public CreateDateJobParameter jobParameter(@Value("#{jobParameters[createDate]}") String createDate) {
		log.info(">>>>>>>>>>>>>> CreateDateJobParameter-- createDate:{}", createDate);
		return new CreateDateJobParameter(createDate);
	}
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Bean(name = JOB_NAME)
	public Job MyBatisCursorItemReaderJob() throws Exception {
		return jobBuilderFactory.get(JOB_NAME)
				.start(myBatisCursorItemReaderStep())
				.build();
	}

	@Bean(name = JOB_NAME + "Step")
	@JobScope
	public Step myBatisCursorItemReaderStep() throws Exception {
		return stepBuilderFactory.get(JOB_NAME + "Step")
				.<Pay, Pay>chunk(chunkSize)
				.reader(myBatisCursorItemReader())
				.writer(myBatisCursorItemWriter())
				.build();				
	}

	@Bean(name = JOB_NAME + "Reader")
	@StepScope
	public MyBatisCursorItemReader<Pay> myBatisCursorItemReader() throws Exception {
		log.info(">>>>>>>>>>>>>>>>>>>> jobParameter createDate:{}", jobParameter.getCreateDate());
		return new MyBatisCursorItemReaderBuilder<Pay>()
				.sqlSessionFactory(sqlSessionFactory)
				.queryId("sfmi.batch.dao.PayDAO.selectPays")
				.build();
	}
	
	@Bean(name = JOB_NAME + "Writer")
	@StepScope
	public ItemWriter<Pay> myBatisCursorItemWriter() throws Exception {
		return list -> {
			for(Pay pay : list) {
				log.info("Current Pay = {}", pay);
			}
		};
	}
	
	

}
