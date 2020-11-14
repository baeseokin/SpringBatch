package sfmi.batch.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import sfmi.batch.dto.Pay;

@Mapper
public interface PayDAO {
	List<Pay> selectPays(Pay pay) throws Exception;
	void updatePay(Pay pay) throws Exception;
}
