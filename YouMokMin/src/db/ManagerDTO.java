package db;

import java.util.ArrayList;

public class ManagerDTO {
	int id;
	String menu;
	String sep;
	int display;
	int displayMax;
	int retailPrice;
	int storage;
	int wholesalePrice;
	
	public ManagerDTO() {
		// TODO Auto-generated constructor stub
	}
	public ManagerDTO(String menu, Integer storage) {
		this.menu = menu;
		this.storage = storage;
	}
	public ManagerDTO(String menu, String column) {
		String[] arr = column.split(",");
		System.out.println(column);
		this.menu = menu;
		this.sep = arr[0];
		this.retailPrice = Integer.parseInt(arr[1]);
		this.storage = Integer.parseInt(arr[2]);
		this.wholesalePrice =Integer.parseInt(arr[3]);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMenu() {
		return menu;
	}
	public void setMenu(String menu) {
		this.menu = menu;
	}
	public String getCategory() {
		return sep;
	}
	public void setCategory(String category) {
		this.sep = category;
	}
	public int getDisplay() {
		return display;
	}
	public void setDisplay(int display) {
		this.display = display;
	}
	public int getDisplayMax() {
		return displayMax;
	}
	public void setDisplayMax(int displayMax) {
		this.displayMax = displayMax;
	}
	public int getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(int retailPrice) {
		this.retailPrice = retailPrice;
	}
	public int getStorage() {
		return storage;
	}
	public void setStorage(int storage) {
		this.storage = storage;
	}
	public int getWholesalePrice() {
		return wholesalePrice;
	}
	public void setWholesalePrice(int wholesalePrice) {
		this.wholesalePrice = wholesalePrice;
	}
}