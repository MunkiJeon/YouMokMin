package db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InvenDTO {
	//menu,catecory,display,displayMax,storage
	Integer id,display,displayMax,storage;
	String menu,category;
	
//	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public InvenDTO() {
		// TODO Auto-generated constructor stub
	}

	public InvenDTO(Integer id, String menu, String category, Integer display, Integer displayMax, Integer storage) {
		super();
		this.id = id;
		this.menu = menu;
		this.category = category;
		this.display = display;
		this.displayMax = displayMax;
		this.storage = storage;
	}
	
	public InvenDTO(String menu, Integer display, Integer storage) {
		super();
		this.menu = menu;
		this.display = display;
		this.storage = storage;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public Integer getDisplay() {
		return display;
	}

	public void setDisplay(Integer display) {
		this.display = display;
	}

	public Integer getDisplayMax() {
		return displayMax;
	}

	public void setDisplayMax(Integer displayMax) {
		this.displayMax = displayMax;
	}

	public Integer getStorage() {
		return storage;
	}

	public void setStorage(Integer storage) {
		this.storage = storage;
	}
	

}
