package sfmi.batch.support;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public abstract class SfmiJobSupport {

	public SfmiJobBuilderFactory jobBuilderFactory;
	public StepBuilderFactory stepBuilderFactory;
	public DataSource dataSource;
	
	@Autowired 
	public SqlSessionFactory sqlSessionFactory;

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
	
	public SfmiJobSupport(SfmiJobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.dataSource = dataSource;
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
	
	public abstract Job run() throws Exception;
}
