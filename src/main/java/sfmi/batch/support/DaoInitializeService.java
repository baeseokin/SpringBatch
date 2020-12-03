package sfmi.batch.support;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.aop.NexusDB;

@Component
@Slf4j
public class DaoInitializeService {
	public String dbName = "fw";
	
	@NexusDB
	public void loadDbProperties(String dbName) {
		this.dbName = dbName;
		log.debug("DaoInitializeService  -- db is loading, Name : {}" ,this.dbName);
		
	}
}
