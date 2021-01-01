package sfmi.batch.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SeqDetail extends BaseDto{
	private int id;
	private String name;
	private String checked;
		
}