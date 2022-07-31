package db;

public class MenuDTO {
	int id;
	String menu;
	String img;
	int price;
	String category;
	String sep;
	
	public MenuDTO() {	
		
	}
	
	public MenuDTO(int id, String menu, String img,
			 		   int price, String category, String sep) {
		this.id = id;
		this.menu = menu;
		this.img = img;
		this.price = price;
		this.category = category;
		this.sep = sep;
		
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

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getCategory() {
		return category;
	}

	public void setCat(String category) {
		this.category = category;
	}

	public String getSep() {
		return sep;
	}

	public void setSep(String sep) {
		this.sep = sep;
	}

	@Override
	public String toString() {
		return "customerDTO [id=" + id + ", menu=" + menu + ", img=" + img + ", price=" + price + ", category=" + category
				+ ", sep=" + sep + "]";
	}
	
	
	
}