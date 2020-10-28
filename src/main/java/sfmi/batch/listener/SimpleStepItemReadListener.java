package sfmi.batch.listener;

import org.apache.ibatis.cursor.Cursor;
import org.springframework.batch.core.ItemReadListener;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;

@Slf4j
public class SimpleStepItemReadListener implements ItemReadListener<Pay>{

	@Override
	public void beforeRead() {
		log.info("%%%%%%%%%%%%%%%%%%%%%% Before Read start");
	}

	@Override
	public void afterRead(Pay pay) {
		log.info("%%%%%%%%%%%%%%%%%%%%%% After Read end, pay:{}",pay);
	}

	@Override
	public void onReadError(Exception ex) {
		log.info("%%%%%%%%%%%%%%%%%%%%%% error info:{}",ex);
	}

}
