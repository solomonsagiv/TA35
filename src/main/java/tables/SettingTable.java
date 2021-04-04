package tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "setting")
public class SettingTable {
	
	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "item")
	private String item;
	@Column(name = "item_data")
	private String item_data;
	
	// Constructor
	public SettingTable(String item, String item_data) {
		this.item = item;
		this.item_data = item_data;
	}

	// Empty constructor
	public SettingTable() {}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getItem() {
		return item;
	}


	public void setItem(String item) {
		this.item = item;
	}


	public String getItem_data() {
		return item_data;
	}


	public void setItem_data(String item_data) {
		this.item_data = item_data;
	}


	@Override
	public String toString() {
		return "SettingTable [id=" + id + ", item=" + item + ", item_data=" + item_data + "]";
	}

	

}
