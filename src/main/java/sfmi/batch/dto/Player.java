package sfmi.batch.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sfmi.batch.aop.NexusDTO;

@Getter
@Setter
@ToString
public class Player extends BaseDto implements Serializable{
	@NexusDTO(size =10, align="left", type="String")
	private String ID;
	@NexusDTO(size =10, align="left", type="String")
    private String lastName;
	@NexusDTO(size =20, align="left", type="String")
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;
}
