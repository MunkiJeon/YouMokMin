package db;

import java.util.Date;

public class CustomerDTO {
	public int id;
	public int locker;
	public String nickname;
	public int password;
	public int subscribe;
	public int price;
	public Date timein;
	public Date timeout;
	public int leftcount;
	public Date dday;
	
	public CustomerDTO() {
		
	}
	
	public CustomerDTO(int id, int locker, String nickname,
					   int password, int subscribe, int price,
					   Date timein, Date timeout, int leftcount) {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLocker() {
		return locker;
	}

	public void setLocker(int locker) {
		this.locker = locker;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getPassword() {
		return password;
	}

	public void setPassword(int password) {
		this.password = password;
	}

	public int getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(int subscribe) {
		this.subscribe = subscribe;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Date getTimein() {
		return timein;
	}

	public void setTimein(Date timein) {
		this.timein = timein;
	}

	public Date getTimeout() {
		return timeout;
	}

	public void setTimeout(Date timeout) {
		this.timeout = timeout;
	}

	public int getLeftcount() {
		return leftcount;
	}

	public void setLeftcount(int leftcount) {
		this.leftcount = leftcount;
	}
	
	
	
	
}