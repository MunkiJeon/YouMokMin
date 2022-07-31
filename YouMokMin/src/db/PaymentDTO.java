package db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentDTO {
	
	Integer id;
	int locker;
	String category;
	Integer menuid;
	String menu;
	int amount;
	int price;
	Date paytime;
	String state;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");//Date paytime를 String으로 바꿔줌
	
	public PaymentDTO(){
		
	}
	
	public PaymentDTO(int id, int locker, String category,
					  int menuid, String menu, int amount,
					  int price, String paytime, String state){
		this.id = id;
		this.locker = locker;
		this.category = category;
		this.menuid = menuid;
		this.menu = menu;
		this.amount = amount;
		this.price = price;
		try {
			this.paytime = sdf.parse(paytime); //Date 타입의 birth를 String으로 바꿔주기위해
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.state = state;
		
	}
	
	public PaymentDTO(String category,int menuid, 
			  		  String menu, int amount, int price){
		
		this.category = category;
		this.menuid = menuid;
		this.menu = menu;
		this.amount = amount;
		this.price = price;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getMenuid() {
		return menuid;
	}

	public void setMenuid(int menuid) {
		this.menuid = menuid;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Date getPaytime() {//결제일시Date로가져옴
		return paytime;
	}
	public String getPaytimeStr() { //결제일시문자열로가져옴
		return sdf.format(paytime);
	}
	public void setPaytime(Date paytime) {
		this.paytime = paytime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	
	
}