package sfmi.batch.job;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;
import sfmi.batch.util.Incrementer;

@Configuration
@RequiredArgsConstructor
@Slf4j

public class SamMulti2DbJob 
/* extends DefaultBatchConfigurer */
{

	public static final String JOB_NAME = "SamMulti2DbJob";

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	/*
	 * @Override protected JobRepository createJobRepository() throws Exception {
	 * MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
	 * factory.setTransactionManager(new ResourcelessTransactionManager());
	 * factory.afterPropertiesSet(); return factory.getObject(); }
	 */

	private int chunkSize = 10;

	@Value("${chunkSize:10}")
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	@Bean(name = JOB_NAME)
	public Job sam2DbJob() throws Exception {
		return jobBuilderFactory.get(JOB_NAME).start(sam2DbStep()).incrementer(new Incrementer()).build();
	}

	@Bean(name = JOB_NAME + "_step")
	@JobScope
	public Step sam2DbStep() throws Exception {
		return stepBuilderFactory.get(JOB_NAME + "_step").<Pay, Pay>chunk(chunkSize).reader(sam2DbItemMultiReader())
				.processor(sam2DbItemProcess()).writer(sam2DbItemWriter()).build();
	}

	@Bean(name = JOB_NAME + "_multiReader")
	@StepScope
	public MultiResourceItemReader<Pay> sam2DbItemMultiReader() {
		MultiResourceItemReader<Pay> multiResourceItemReader = new MultiResourceItemReader<Pay>();
		multiResourceItemReader.setDelegate(sam2DbItemReader());
		
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = null;
		try {
			resources = resolver.getResources("classpath:pay*.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		multiResourceItemReader.setResources(resources);
		return multiResourceItemReader;
	}
	
	@Bean(name = JOB_NAME + "_reader")
	@StepScope
	public FlatFileItemReader<Pay> sam2DbItemReader() {
		FlatFileItemReader<Pay> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("pay.txt"));
		

		DefaultLineMapper defaultLineMapper = new DefaultLineMapper();
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames(new String[] { "ID", "amount", "tx_name" });

		BeanWrapperFieldSetMapper<Pay> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Pay.class);

		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		reader.setLineMapper(defaultLineMapper);
		reader.setLinesToSkip(1);

		return reader;
	}

	@Bean
	@StepScope
	public ItemProcessor<Pay, Pay> sam2DbItemProcess() {
		log.info("Processor start!!!!");
		return item -> {
			item.setTxName(item.getTxName()+"1");
			return item;
		};
	}

	@Bean(name = JOB_NAME + "_writer")
	@StepScope

	  public MyBatisBatchItemWriter<Pay> sam2DbItemWriter() throws Exception {
	  
		MyBatisBatchItemWriter<Pay> myBatchItemWriter = new MyBatisBatchItemWriter<Pay>();
		myBatchItemWriter.setSqlSessionFactory(sqlSessionFactory);
		myBatchItemWriter.setStatementId("sfmi.batch.dao.PayDAO.updatePay");
	  
		return myBatchItemWriter;
	  
	  }
 

}
