package sfmi.batch.util;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("deprecation")
public class Incrementer implements JobParametersIncrementer {

    public JobParameters getNext(JobParameters parameters) {
        if (parameters==null || parameters.isEmpty()) {
        	log.info("JobParameters Incrementer parameter is null >>>>>> run.id = 1");
            return new JobParametersBuilder().addLong("run.id", 1L).toJobParameters();
        }
        
		long id = parameters.getLong("run.id",1L) + 1;

    	log.info("JobParameters Incrementer parameter is not null , before:{}, after:{}", parameters.getLong("run.id",1L), id);

        return new JobParametersBuilder().addLong("run.id", id).toJobParameters();
    }
}
