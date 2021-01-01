package sfmi.batch;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@DependsOn("multiTxManager")
@EnableTransactionManagement
public class XaForDb1DataManagerConfig {

	@Value("${spring.jta.atomikos.datasource.db1.unique-resource-name}") 
	private String uniqueResourceName; 
	@Value("${spring.jta.atomikos.datasource.db1.xa-data-source-class-name}") 
	private String dataSourceClassName; 
	@Value("${spring.jta.atomikos.datasource.db1.xa-properties.user}") 
	private String user; 
	@Value("${spring.jta.atomikos.datasource.db1.xa-properties.password}") 
	private String password; 
	@Value("${spring.jta.atomikos.datasource.db1.xa-properties.url}") 
	private String url;

	@Bean(name = "xaForDb1DataSource")
	public DataSource xaForDb1DataSource() {
		Properties properties = new Properties();
		properties.setProperty("url", url);
		properties.setProperty("user", user);
		properties.setProperty("password", password);
		
		AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
		dataSource.setUniqueResourceName(uniqueResourceName);
		dataSource.setXaDataSourceClassName(dataSourceClassName);
		dataSource.setXaProperties(properties);
		
		return dataSource;
	}
	
	@Bean
	@Primary
	public SqlSessionFactory sqlSessionFactory1(@Qualifier("xaForDb1DataSource") DataSource dataSource1) throws Exception{
		
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource1);
		
		Resource[] mapperRes = new PathMatchingResourcePatternResolver().getResources("classpath:mappers/SeqMapper1.xml");
		Resource configLocationRes = new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml");
		sessionFactory.setMapperLocations(mapperRes);
		sessionFactory.setConfigLocation(configLocationRes);
		
		return sessionFactory.getObject();
	}
	
	@Bean
	@Primary
	public SqlSessionTemplate sqlSessionTemplate1(SqlSessionFactory sqlSessionFactory1) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory1);
	}	
}
