package sfmi.batch.job;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.TestBatchConfig;

@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes= {SimpleJobConfiguration.class, TestBatchConfig.class})
@Slf4j
class SimpleJobConfigurationTest {
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	

	@Test
	void testSimpleJob() throws Exception {
		String requestDate = "20201020";
		
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("requestDate", requestDate)	
				.toJobParameters();
				
		log.info(">>> jobParameters : {}", jobParameters);
		log.info(">>> jobLauncherTestUtils : {}", jobLauncherTestUtils);
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
		log.info(">>>staus:{}", jobExecution.getStatus());
		assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
	}

}
