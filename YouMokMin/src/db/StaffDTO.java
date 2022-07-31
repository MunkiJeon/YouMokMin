package db;

public class StaffDTO {
	public Integer id;
	public String userName;
	public String userId;
	public Integer userPw;
	
	public String toString() {
		return id+"/"+userName+"/"+userId+"/"+userPw;

	}

}