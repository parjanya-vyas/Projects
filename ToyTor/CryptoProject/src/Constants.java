
public class Constants {
	public static final byte TYPE_CREATE = 0;
	public static final byte TYPE_EXTEND = 1;
	public static final byte TYPE_EXTENDED = 2;
	public static final byte TYPE_RELAY = 3;
	public static final byte TYPE_DESTROY = 4;
	
	public static final byte DIRECTION_FORWARD = 0;
	public static final byte DIRECTION_REVERSE = 1;
	
	public static final int INDEX_CIRCUIT_ID = 0;
	public static final int INDEX_RECEIVING_PORT_START = 1;
	public static final int INDEX_MESSAGE_TYPE = 3;
	public static final int INDEX_IP_START = 4;
	public static final int INDEX_PORT_START = 8;
	
	public static final int SIZE_IP_BYTES = 4;
	public static final int SIZE_PORT_BYTES = 2;
	public static final int SIZE_RECEIVING_PORT_BYTES = 2;
	
	public static final byte[] IV = {7,5,9,2,6,4,5,1,7,3,6,8,2,4,9,0};
}
