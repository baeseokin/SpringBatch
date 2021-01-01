package sfmi.batch.aop;

import java.util.Map;

public class NexusDTOInfo {

	private String dtoClassName;
	private Map<String, NexusDTOColumnInfo> columnInfoList;
	public String getDtoClassName() {
		return dtoClassName;
	}
	public Map<String, NexusDTOColumnInfo> getColumnInfoList() {
		return columnInfoList;
	}
	public void setDtoClassName(String dtoClassName) {
		this.dtoClassName = dtoClassName;
	}
	public void setColumnInfoList(Map<String, NexusDTOColumnInfo> columnInfoList) {
		this.columnInfoList = columnInfoList;
	}
	
	@Override
	public String toString() {
		return "NexusDTOInfo [dtoClassName=" + dtoClassName + ", columnInfoList=" + columnInfoList + "]";
	}
	
	
	
}
