package sfmi.batch.job;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Player;
import sfmi.batch.util.Incrementer;

@Configuration
@EnableBatchProcessing
@Slf4j
public class Sam2FixedSamJob{
	
	public static final String JOB_NAME ="Sam2FixedSamJob";
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	private int chunkSize = 10;
	
	@Value("${chunkSize:10}")
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	
	@Bean(name = JOB_NAME)
	public Job sam2FixedSamJob() {
		return jobBuilderFactory.get(JOB_NAME)
				.start(sam2FixedSamStep())
				.incrementer(new Incrementer())
				.build();
	}
	@Bean(name = JOB_NAME + "_step")
	@JobScope
	public Step sam2FixedSamStep() {
		return stepBuilderFactory.get(JOB_NAME + "_step")
				.<Player, Player> chunk(chunkSize)
				.reader(sam2FixedSamItemReader())
				.writer(sam2FixedSamItemWriter())
				.build();
	}
	@Bean(name = JOB_NAME +"_reader")
	@StepScope
	public FlatFileItemReader<Player> sam2FixedSamItemReader() {
		FlatFileItemReader<Player> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("player.txt"));
		
		DefaultLineMapper defaultLineMapper = new DefaultLineMapper();
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames(new String[] {"ID","lastName","firstName","position","birthYear","debutYear"});
		
		BeanWrapperFieldSetMapper<Player> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Player.class);
		
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		reader.setLineMapper(defaultLineMapper);
		reader.setLinesToSkip(1);
		
		return reader;
	}

	@Bean(name = JOB_NAME +"_writer")
	@StepScope
	public FlatFileItemWriter<Player> sam2FixedSamItemWriter(){
		
		  BeanWrapperFieldExtractor<Player> fieldExtractor = new  BeanWrapperFieldExtractor<>(); 
		  fieldExtractor.setNames(new String[]{"ID","lastName","firstName","position","birthYear","debutYear"});
		  fieldExtractor.afterPropertiesSet();
		  
		  FormatterLineAggregator<Player> lineAggregator = new FormatterLineAggregator<>(); 
		  lineAggregator.setFormat("%-20s,%-20s,%-20s,%-20s,%-20s,%-20s");
		  lineAggregator.setFieldExtractor(fieldExtractor);
		  
		  FlatFileItemWriter writer = new FlatFileItemWriter();
		  writer.setResource(new FileSystemResource("target/players_out.txt"));
		  writer.setLineAggregator(lineAggregator);
	
		  return writer;
	}
		
		
}
