package db;

import java.io.Serializable;

public class WaitingCustomer implements Serializable{
	
	private static final long serialVersionUID = 23450L;
	
	public int locker;
	public String menu;
	public String state;

}