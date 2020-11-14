package sfmi.batch.support;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import sfmi.batch.listener.SfmiSimpleJobListener;
import sfmi.batch.util.Incrementer;

@Primary
@Component
public class SfmiJobBuilderFactory extends JobBuilderFactory{
	
	@Autowired
	private JobRepository jobRepository;

	public SfmiJobBuilderFactory(JobRepository jobRepository) {
		super(jobRepository);
		// TODO Auto-generated constructor stub
	}

	@Override	
	public JobBuilder get(String name) {
		JobBuilder builder = new JobBuilder(name).repository(jobRepository);
		builder.listener(new SfmiSimpleJobListener());   //Sfmi Listener 추가
		builder.incrementer(new Incrementer());          //파라메터 run.id 증가
		return builder;
	}
}
