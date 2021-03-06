package sfmi.batch.job;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.TestBatchConfig;
import sfmi.batch.dao.PayDAO;
import sfmi.batch.dto.Pay;
import sfmi.batch.job.MyBatisCursorItemReaderJobConfiguration;

@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes= {MultiThreadJdbcPagingItemReaderJobConfiguration.class, TestBatchConfig.class})
@Slf4j
@ActiveProfiles("mysql")
@TestPropertySource(properties = {"chunkSize=2", "poolSize=2"})
class MultiThreadJdbcPagingItemReaderJobConfigurationTest {
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	
	@Test
	void testMultiThreadJdbcPagingItemReaderJob() throws Exception {
		log.info(">>> start MultiThreadJdbcPagingItemReaderJob");
		
		String createDate = "20201020";
		
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("createDate", createDate)
				.addString("version", "3")				
				.toJobParameters();
				
		log.info(">>> jobParameters : {}", jobParameters);
		log.info(">>> jobLauncherTestUtils : {}", jobLauncherTestUtils);
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
		log.info(">>>staus:{}", jobExecution.getStatus());
		assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
	}


}
