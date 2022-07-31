package db;

import java.io.Serializable;

public class WaitingCustomer implements Serializable{

	private static final long serialVersionUID = 06151225L;

	public int id;
	public int locker;
	public String menu;
	public String state;


	public WaitingCustomer() {
	}

	@Override
	public String toString() {
		return "WaitingCustomer [id = "+id+", locker=" + locker + ", menu=" + menu + ", state=" + state;
	}
	
	
}