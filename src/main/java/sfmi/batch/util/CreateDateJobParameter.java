package sfmi.batch.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class CreateDateJobParameter {
	private LocalDate createDate;
	
	public CreateDateJobParameter(String createDate) {
		if(createDate != null) this.createDate = LocalDate.parse(createDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
	}
}
