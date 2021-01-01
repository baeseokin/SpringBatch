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
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@DependsOn("multiTxManager")
@EnableTransactionManagement
public class XaForDb2DataManagerConfig {

	@Value("${spring.jta.atomikos.datasource.db2.unique-resource-name}") 
	private String uniqueResourceName; 
	@Value("${spring.jta.atomikos.datasource.db2.xa-data-source-class-name}") 
	private String dataSourceClassName; 
	@Value("${spring.jta.atomikos.datasource.db2.xa-properties.user}") 
	private String user; 
	@Value("${spring.jta.atomikos.datasource.db2.xa-properties.password}") 
	private String password; 
	@Value("${spring.jta.atomikos.datasource.db2.xa-properties.url}") 
	private String url;

	
	@Bean(name = "xaForDb2DataSource")
	public DataSource xaForDb2DataSource() {
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
	
	@Bean(name ="sqlSessionFactory2")
	public SqlSessionFactory sqlSessionFactory2(@Qualifier("xaForDb2DataSource") DataSource dataSource2) throws Exception{
		
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource2);
		
		Resource[] mapperRes = new PathMatchingResourcePatternResolver().getResources("classpath:mappers/SeqMapper2.xml");
		Resource configLocationRes = new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml");
		sessionFactory.setMapperLocations(mapperRes);
		sessionFactory.setConfigLocation(configLocationRes);
		
		return sessionFactory.getObject();
	}
	
	@Bean
	public SqlSessionTemplate sqlSessionTemplate2(SqlSessionFactory sqlSessionFactory2) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory2);
	}
}
