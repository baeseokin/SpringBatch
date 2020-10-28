package sfmi.batch.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import sfmi.batch.dto.Player;

public class PlayerMapper implements FieldSetMapper<Player>{

	@Override
	public Player mapFieldSet(FieldSet fieldSet) throws BindException {
		if(fieldSet == null){
	           return null;
	       }

	       Player player = new Player();
		/*
		 * player.setID(fieldSet.readString("ID"));
		 * player.setLastName(fieldSet.readString("lastName"));
		 * player.setFirstName(fieldSet.readString("firstName"));
		 * player.setPosition(fieldSet.readString("position"));
		 * player.setDebutYear(fieldSet.readInt("debutYear"));
		 * player.setBirthYear(fieldSet.readInt("birthYear"));
		 */
	       player.setID(fieldSet.readString(0));
	       player.setLastName(fieldSet.readString(1));
	       player.setFirstName(fieldSet.readString(2));
	       player.setPosition(fieldSet.readString(3));
	       player.setDebutYear(fieldSet.readInt(4));
	       player.setBirthYear(fieldSet.readInt(5));

	       return player;
	}

}
