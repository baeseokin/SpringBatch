package sfmi.batch.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sfmi.batch.aop.NexusDTO;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Pay extends BaseDto{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NexusDTO(size =10, align="left", type="String")
	private Long id;
	@NexusDTO(size =10, align="left", type="String")
	private Long amount;
	@NexusDTO(size =10, align="left", type="String")
	private String txName;
	
    public Pay(Long amount, String txName) {
    	this.amount = amount;
        this.txName = txName;
    }

    public Pay(Long id, Long amount, String txName) {
    	this.id = id;
        this.amount = amount;
        this.txName = txName;
    }	
	
	
}