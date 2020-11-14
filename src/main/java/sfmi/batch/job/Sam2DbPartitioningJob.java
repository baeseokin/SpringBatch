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
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;
import sfmi.batch.support.CustomMultiResourcePartitioner;
import sfmi.batch.support.SfmiJobBuilderFactory;
import sfmi.batch.support.SfmiJobSupport;
import sfmi.batch.util.Incrementer;

@Configuration
@Slf4j
@ConditionalOnProperty(name="job.name", havingValue = "Sam2DbPartitioningJob")
public class Sam2DbPartitioningJob  extends SfmiJobSupport{
/* extends DefaultBatchConfigurer */

	public Sam2DbPartitioningJob(SfmiJobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
		super(jobBuilderFactory, stepBuilderFactory, dataSource);
		// TODO Auto-generated constructor stub
	}

	public static final String JOB_NAME = "Sam2DbPartitioningJob";
	
	
	
	
	@Bean
	public CustomMultiResourcePartitioner partitioner() {
		CustomMultiResourcePartitioner partitioner = new CustomMultiResourcePartitioner();
        Resource[] resources;
        ResourcePatternResolver resoursePatternResolver =  new PathMatchingResourcePatternResolver();
        try {
            resources = resoursePatternResolver.getResources("file:src/main/resources/pay*.txt");
        } catch (IOException e) {
            throw new RuntimeException("I/O problems when resolving the input file pattern.", e);
        }
        partitioner.setResources(resources);
        return partitioner;
	}

	@Bean(name = JOB_NAME)
	public Job run() throws Exception {
		log.info("start run!!!!!!!!!!!!!");
		return jobBuilderFactory.get(JOB_NAME)
				.start(partitionStep())
				.build();
	}
	
	@Bean(name = JOB_NAME + "_PartitionStep")
	@JobScope
	public Step partitionStep()  throws Exception {
		log.info("start partitionStep!!!!!!!!!!!!!");
		return stepBuilderFactory.get(JOB_NAME + "_PartitionStep")
				.partitioner(JOB_NAME + "_step", partitioner())
				.step(Sam2DbPartitioningStep())
				.taskExecutor(executor())
				.build();
		
	}
	@Bean(name = JOB_NAME + "_step")
	public Step Sam2DbPartitioningStep() throws Exception {
		return stepBuilderFactory.get(JOB_NAME + "_step")
				.<Pay, Pay>chunk(chunkSize)
				.reader(sam2DbItemReader(null))
				.processor(sam2DbItemProcess())
				.writer(sam2DbItemWriter())
				.build();
	}


	@Bean(name = JOB_NAME + "_reader")
	@StepScope
	public FlatFileItemReader<Pay> sam2DbItemReader(@Value("#{stepExecutionContext[fileName]}") String fileName) throws Exception {
		FlatFileItemReader<Pay> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource(fileName));

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
