package sfmi.batch.dao;

import org.apache.ibatis.annotations.Mapper;

import sfmi.batch.dto.Seq;
import sfmi.batch.dto.SeqDetail;

@Mapper
public interface SeqDAO1 {
	Seq selectSeq(Seq seq) throws Exception;
	void insertSeq(Seq seq) throws Exception;
}
