package sfmi.batch.job;

import javax.annotation.Resource;

import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dao.SeqDAO1;
import sfmi.batch.dto.Seq;
import sfmi.batch.dto.SeqDetail;
import sfmi.batch.listener.SfmiSimpleStepItemWriteListener;
import sfmi.batch.support.SfmiJobSupport;
import sfmi.batch.util.Incrementer;

@Configuration
@Slf4j
@ConditionalOnProperty(name="job.name", havingValue = MultiTranJob.JOB_NAME)
public class MultiTranJob extends SfmiJobSupport{

	public static final String JOB_NAME ="MultiTranJob";
	
	@Resource(name = "seqDAO1")
	public SeqDAO1 seqDAO1;
	
	@Bean(name = JOB_NAME)
	public Job run(Step multiTranStep) throws Exception {

		return jobBuilderFactory.get(JOB_NAME)
				.start(multiTranStep)
				.incrementer(new Incrementer())
				.build();
	}
	@Bean(name = JOB_NAME + "step")
	public Step multiTranStep(	FlatFileItemReader<Seq> multiTranItemReader, 
								ItemProcessor<Seq,SeqDetail> multiTranItemProcess, 
								MyBatisBatchItemWriter<SeqDetail> multiTranItemWriter ) throws Exception {
		return stepBuilderFactory.get(JOB_NAME + "_step")
				.<Seq, SeqDetail> chunk(chunkSize)
				.faultTolerant() 
				.skip(Exception.class) 
				.skipLimit(10)
				.reader(multiTranItemReader)
				.processor(multiTranItemProcess)
				.writer(multiTranItemWriter)
				.listener(new SfmiSimpleStepItemWriteListener())
				.build();
	}
	@Bean
	public FlatFileItemReader<Seq> multiTranItemReader() {
		FlatFileItemReader<Seq> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("seq.txt"));
		
		DefaultLineMapper defaultLineMapper = new DefaultLineMapper();
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames(new String[] {"id"});
		
		BeanWrapperFieldSetMapper<Seq> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Seq.class);
		
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		reader.setLineMapper(defaultLineMapper);
		
		return reader;
	}
	@Bean
	public ItemProcessor<Seq,SeqDetail> multiTranItemProcess() {
		return item -> {
			log.info("ItemProcessor, insertSeq : {}", item);
			seqDAO1.insertSeq(item);
			
			SeqDetail detail = new SeqDetail();
			detail.setId(item.getId());
			detail.setName("SEQ"+item.getId());
			detail.setChecked("Y");
			
			if(item.getId()==55) {
				detail.setName("SEQ1234567890");
			}
			
			return detail;
		};
	}	
	@Bean
	public MyBatisBatchItemWriter<SeqDetail> multiTranItemWriter() throws Exception {
		  
		MyBatisBatchItemWriter<SeqDetail> myBatchItemWriter = new MyBatisBatchItemWriter<SeqDetail>();
		myBatchItemWriter.setSqlSessionFactory(sqlSessionFactory2);
		myBatchItemWriter.setStatementId("sfmi.batch.dao.SeqDAO2.insertSeqDetail");
		myBatchItemWriter.setAssertUpdates(false);
	  
		return myBatchItemWriter;
	  
	}
		
		
}
