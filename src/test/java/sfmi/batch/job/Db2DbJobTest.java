package sfmi.batch.job;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sfmi.batch.support.TestBatchLegacyConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableBatchProcessing
@ContextConfiguration(classes={Db2DbJob.class, TestBatchLegacyConfig.class}) 
public class Db2DbJobTest {
	@Autowired
    private JobLauncherTestUtils jobLauncherTestUtils; // (2)


	@Test
	public void test() {
		JobExecution jobExecution = null;
		try {
			jobExecution = jobLauncherTestUtils.launchJob();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // (3)

        //then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
	}

}
