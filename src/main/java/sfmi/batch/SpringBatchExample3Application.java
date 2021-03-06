package sfmi.batch;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.atomikos.jdbc.AtomikosDataSourceBean;

import lombok.extern.slf4j.Slf4j;

@EnableBatchProcessing
@SpringBootApplication
@EnableAutoConfiguration(exclude= {DataSourceAutoConfiguration.class})
@Slf4j
public class SpringBatchExample3Application {
	public static void main(String[] args) {
		int exit = SpringApplication.exit(SpringApplication.run(SpringBatchExample3Application.class, args));
		log.info("exitCode={}", exit);
		System.exit(exit);
	}
	
	@Value("${spring.batch.job.names:NONE}")
	private String jobNames;
	
	@PostConstruct
	public void validateJobNames() {
		log.info("jobNames : {}", jobNames);
		if(jobNames.isEmpty() || jobNames.equals("NONE")) {
			throw new IllegalStateException("spring.batch.job.names=job1,job2 형태로 실행을 원하는 JOB을 명시해야함 합니다.");
		}
	}
	
	@Bean
	@ConfigurationProperties(prefix="spring.datasource.meta")
	@Primary
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}
	
	
}
