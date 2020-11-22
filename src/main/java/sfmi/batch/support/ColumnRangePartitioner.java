package sfmi.batch.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class ColumnRangePartitioner implements Partitioner{

	private SqlSessionFactory sqlSessionFactory;
	
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
    }
	
	public int getMinValue(SqlSessionFactory sqlSessionFactory) throws Exception{
		SqlSession session = sqlSessionFactory.openSession();
		return session.selectOne("sfmi.batch.dao.PayDAO.selectMinValue");
	}
	
	public int getMaxValue(SqlSessionFactory sqlSessionFactory) throws Exception{
		SqlSession session = sqlSessionFactory.openSession();
		return session.selectOne("sfmi.batch.dao.PayDAO.selectMaxValue");
	}	
	
	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		int min=0;
		try {
			min = getMinValue(sqlSessionFactory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int max=0;
		try {
			max = getMaxValue(sqlSessionFactory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int targetSize = (max-min) / gridSize + 1;
		
		Map<String, ExecutionContext> result = new HashMap<>();
		
		int number = 0;
        int start = min;
        int end = start + targetSize - 1;
         
        while (start <= max) 
        {
            ExecutionContext value = new ExecutionContext();
            result.put("partition" + number, value);
             
            if(end >= max) {
                end = max;
            }
             
            value.putInt("minValue", start);
            value.putInt("maxValue", end);
 
            start += targetSize;
            end += targetSize;
 
            number++;
        }
        return result;
	}
	

}
