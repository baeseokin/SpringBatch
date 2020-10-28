package sfmi.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Player;
import sfmi.batch.mapper.PlayerMapper;
import sfmi.batch.util.Incrementer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FlatFileItemReaderJobConfiguration extends DefaultBatchConfigurer {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	private static final int chunkSize = 10;
	
	@Override
	protected JobRepository createJobRepository() throws Exception {
		MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
		factory.setTransactionManager(new ResourcelessTransactionManager());
		factory.afterPropertiesSet();
		return factory.getObject();
	}
	
	@Bean
	public Job flatFileItemReaderJob() {
		return jobBuilderFactory.get("flatFileItemReaderJob")
				.start(flatFileItemReaderStep())
				.incrementer(new Incrementer())
				.build();
	}
	@Bean
	public Step flatFileItemReaderStep() {
		return stepBuilderFactory.get("flatFileItemReaderStep")
				.<Player, Player> chunk(chunkSize)
				.reader(flatFileItemReader())
				.writer(flatFileItemWriter())
				.build();
	}
	@Bean
	public FlatFileItemReader<Player> flatFileItemReader() {
		return new FlatFileItemReaderBuilder<Player>()
				.name("flatFileItemReader")
				.resource(new ClassPathResource("player.txt"))
				.linesToSkip(1)
				.fieldSetMapper(new PlayerMapper())
				.lineTokenizer(new DelimitedLineTokenizer())
				.build();
	}

	public FlatFileItemWriter<Player> flatFileItemWriter() {
		
		BeanWrapperFieldExtractor<Player> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[] {"ID","lastName","firstName","position","birthYear","debutYear"});
		fieldExtractor.afterPropertiesSet();

		DelimitedLineAggregator<Player> lineAggregator = new DelimitedLineAggregator<>();
		lineAggregator.setDelimiter("|");
		lineAggregator.setFieldExtractor(fieldExtractor);
		
		return  new FlatFileItemWriterBuilder<Player>()
       			.name("flatFileItemWriter")
       			.resource(new FileSystemResource("target/players_out.txt"))
       			.lineAggregator(lineAggregator)
       			.build();
	}	
}
