package sfmi.batch.listener;

import java.util.List;

import org.apache.ibatis.cursor.Cursor;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;

import lombok.extern.slf4j.Slf4j;
import sfmi.batch.dto.Pay;
import sfmi.batch.dto.SeqDetail;

@Slf4j
public class SfmiSimpleStepItemWriteListener implements ItemWriteListener<SeqDetail>{@Override
	public void beforeWrite(List<? extends SeqDetail> items) {
		System.out.println(">>>>>>>>>>SfmiSimpleStepItemWriteListener - beforeWrite : "+items);
		
	}

	@Override
	public void afterWrite(List<? extends SeqDetail> items) {
		System.out.println(">>>>>>>>>>SfmiSimpleStepItemWriteListener - afterWrite : "+items);
		
	}

	@Override
	public void onWriteError(Exception exception, List<? extends SeqDetail> items) {
		System.out.println(">>>>>>>>>>SfmiSimpleStepItemWriteListener - onWriteError : exception :"+exception+", items:"+items);
		
	}



}
