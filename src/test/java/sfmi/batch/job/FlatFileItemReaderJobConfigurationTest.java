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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.TestBatchConfig;

@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes= {FlatFileItemReaderJobConfiguration.class, TestBatchConfig.class})
@Slf4j
@ActiveProfiles("mysql")
class FlatFileItemReaderJobConfigurationTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	
	@Test
	void test() throws Exception {
		log.info(">>> start testFlatFileItemReaderJob");
		//LocalDate createDate = LocalDate.of(2020,10,21);
		String createDate = "20201020";
		
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("createDate", createDate)
				.addString("version", "16")				
				.toJobParameters();
				
		log.info(">>> jobParameters : {}", jobParameters);
		log.info(">>> jobLauncherTestUtils : {}", jobLauncherTestUtils);
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
		log.info(">>>staus:{}", jobExecution.getStatus());
		assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
	}


}
