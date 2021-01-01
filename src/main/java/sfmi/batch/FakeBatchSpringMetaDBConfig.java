package sfmi.batch;

import javax.annotation.PostConstruct;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;

import sfmi.batch.support.AtomikosJtaPlatform;

@Configuration
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
	
	@Bean(name = "userTransaction")
	public UserTransaction userTransaction() throws Throwable {
		System.out.println("========= userTransaction =========");
		UserTransactionImp userTransactionImp = new UserTransactionImp();
		userTransactionImp.setTransactionTimeout(10000);
		return userTransactionImp;
	}

	@Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
	public TransactionManager atomikosTransactionManager() throws Throwable {
		System.out.println("========= atomikosTransactionManager =========");
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		userTransactionManager.setForceShutdown(false);
		AtomikosJtaPlatform.transactionManager = userTransactionManager;
		return userTransactionManager;
	}

	@Bean(name = "multiTxManager")
	@DependsOn({ "userTransaction", "atomikosTransactionManager" })
	public PlatformTransactionManager transactionManager() throws Throwable {
		System.out.println("========= transactionManager =========");
		UserTransaction userTransaction = userTransaction();
		AtomikosJtaPlatform.transaction = userTransaction;
		JtaTransactionManager manager = new JtaTransactionManager();
		manager.setTransactionManager(atomikosTransactionManager());
		manager.setUserTransaction(userTransaction);
		manager.setAllowCustomIsolationLevels(true);
		return manager;
	}
	
	@PostConstruct
	void initalize() throws Exception {
		System.out.println("FakeBatchSpringMetaDBConfig -- 1.tractionManager:"+this.tractionManager);
		if(this.tractionManager == null) {
			this.tractionManager = new ResourcelessTransactionManager();
		}
		try {
			this.tractionManager = transactionManager();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("FakeBatchSpringMetaDBConfig -- 2.tractionManager:"+this.tractionManager);
		
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
