package server;

import java.io.Serializable;
import java.util.Arrays;

public class TCPData implements Serializable{
	
	private static final long serialVersionUID = 1234L;
	
	public Object sendObject;
	public String kind;
	public String from;
	public String[] to;
	
	public TCPData(Object sendObject, String kind, String from, String ...to) {
		this.sendObject = sendObject;
		this.kind = kind;
		this.from = from;
		this.to = to;
	}
	
	public TCPData() {
	}
	
	@Override
	public String toString() {
		return kind+", "+from+", "+Arrays.toString(to)+", "+sendObject;
	}
}
