package sfmi.batch.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Pay {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long amount;
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