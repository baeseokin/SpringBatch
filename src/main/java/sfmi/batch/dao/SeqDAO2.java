package sfmi.batch.dao;

import org.apache.ibatis.annotations.Mapper;

import sfmi.batch.dto.Seq;
import sfmi.batch.dto.SeqDetail;

@Mapper
public interface SeqDAO2 {
	SeqDetail selectSeqDetail(SeqDetail seqDetail) throws Exception;
	void insertSeqDetail(SeqDetail seqDetail) throws Exception;
}
