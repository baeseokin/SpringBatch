package sfmi.batch.support;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import sfmi.batch.aop.MyContextContainer;
import sfmi.batch.aop.NexusDB;

@Component
public class SfmiJobSupport {
	@Autowired 
	public SfmiJobBuilderFactory jobBuilderFactory;
	@Autowired 
	public StepBuilderFactory stepBuilderFactory;
	/*
	 * @Autowired public DataSource dataSource1;
	 * 
	 * @Autowired public DataSource dataSource2;
	 */
	
	@Autowired 
	public SqlSessionFactory sqlSessionFactory1;
	
	@Autowired
	@Qualifier("sqlSessionFactory2")
	public SqlSessionFactory sqlSessionFactory2;
	
	
	

	public int chunkSize;
	public int poolSize;
	
	@Value("${chunkSize:10}")
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	@Value("${poolSize:10}")
	public void setPoolSizee(int poolSize) {
		this.poolSize = poolSize;
	}		
	
	public TaskExecutor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(poolSize);
		executor.setMaxPoolSize(poolSize);
		executor.setThreadNamePrefix("multi-thread-");
		executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
		executor.initialize();
				
		return executor;
	}	


}
