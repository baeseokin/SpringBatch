package sfmi.batch;

import javax.annotation.PostConstruct;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

//@Configuration
public class FakeBatchSpringMetaDBConfig implements BatchConfigurer{
	
	PlatformTransactionManager tractionManager;
	JobRepository jobRepository;
	JobLauncher jobLauncher;
	JobExplorer jobExplorer;
	
	@Override
	public JobRepository getJobRepository(){
		return jobRepository;
	}	

	@Override
	public PlatformTransactionManager getTransactionManager() throws Exception {
		return tractionManager;
	}

	@Override
	public JobLauncher getJobLauncher() throws Exception {
		return jobLauncher;
	}

	@Override
	public JobExplorer getJobExplorer() throws Exception {
		return jobExplorer;
	}
	
	@PostConstruct
	void initalize() throws Exception {
		if(this.tractionManager == null) {
			this.tractionManager = new ResourcelessTransactionManager();
		}
		
		MapJobRepositoryFactoryBean jobRepositoryFactory = new MapJobRepositoryFactoryBean(this.tractionManager);
		jobRepositoryFactory.afterPropertiesSet();
		this.jobRepository = jobRepositoryFactory.getObject();
		
		MapJobExplorerFactoryBean jobExplorerFactoryBean = new MapJobExplorerFactoryBean(jobRepositoryFactory);
		jobExplorerFactoryBean.afterPropertiesSet();
		this.jobExplorer = jobExplorerFactoryBean.getObject();
		this.jobLauncher = createJobLauncher();
	}

	private JobLauncher createJobLauncher() throws Exception{
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.afterPropertiesSet();
		
		return jobLauncher;
	}
}
