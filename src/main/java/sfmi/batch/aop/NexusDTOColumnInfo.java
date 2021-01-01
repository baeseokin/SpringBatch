package sfmi.batch.aop;

public class NexusDTOColumnInfo {

	private String align;
	private String type;
	private int size;
	
	public String getAlign() {
		return align;
	}
	public String getType() {
		return type;
	}
	public int getSize() {
		return size;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setSize(int size) {
		this.size = size;
	}
	@Override
	public String toString() {
		return "NexusDTOInfo [align=" + align + ", type=" + type + ", size=" + size + "]";
	}
	
}
