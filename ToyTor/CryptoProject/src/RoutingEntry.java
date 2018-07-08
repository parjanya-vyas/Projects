public class RoutingEntry {
	private String addr;
	private int circId;
	
	public String getAddr() {
		return addr;
	}

	public int getCircId() {
		return circId;
	}
	
	public RoutingEntry(String addr, int circId) {
		this.addr = addr;
		this.circId = circId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj==null || this.getClass()!=obj.getClass())
			return false;
		
		final RoutingEntry other = (RoutingEntry)obj;
		return (addr.equals(other.getAddr()) && circId==other.getCircId());
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31*hash + circId;
		hash = 31*hash + (addr==null ? 0 : addr.hashCode());
		
		return hash;
	}
	
	@Override
	public String toString() {
		return (addr + ";" + circId);
	}
}
