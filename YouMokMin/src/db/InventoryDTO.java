package db;

public class InventoryDTO {
	int id;
	String menu;
	String category;
	int display;
	int displayMax;
	int storage;
	
	
	
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
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
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
	public int getStorage() {
		return storage;
	}
	public void setStorage(int storage) {
		this.storage = storage;
	}
	
	
	
}