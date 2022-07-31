package db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BathDTO {
	// 테이블 컬럼들 
	Integer id;
	Integer locker;
	String nickname;
	String password;
	Integer subscribe; // 일일0, 정기 1
	Integer price;
	Date timein;
	Date timeout;
	Integer leftcount; // 정기권 잔여횟수
	
										// 이규칙으로 날짜가 들어감 

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLocker() {
		return locker;
	}

	public void setLocker(Integer locker) {
		this.locker = locker;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(Integer subscribe) {
		this.subscribe = subscribe;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Date getTimein() {
		return timein;
	}

	public void setTimein(Date timein) {
		this.timein = timein;
	}

	public Date getTimout() {
		return timeout;
	}

	public void setTimout(Date timout) {
		this.timeout = timout;
	}

	public Integer getLeftcount() {
		return leftcount;
	}

	public void setLeftcount(Integer leftcount) {
		this.leftcount = leftcount;
	}

	@Override
	public String toString() {
		return "bathDTO [id=" + id + ", locker=" + locker + ", nickname=" + nickname + ", password=" + password
				+ ", subscribe=" + subscribe + ", price=" + price + ", timein=" + timein + ", timout=" + timeout
				+ ", leftcount=" + leftcount + "]";
	}

	
	
	
}