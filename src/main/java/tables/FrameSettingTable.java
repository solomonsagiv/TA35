package tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "frame_setting")
public class FrameSettingTable {

	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "frame_name")
	private String frameName;
	@Column(name = "bounds")
	private String bounds;
	
	// Constructor
	public FrameSettingTable(String bounds) {
		this.bounds = bounds;
	}
	
	// Empty constructor 
	public FrameSettingTable() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getFrameName() {
		return frameName;
	}

	public void setFrameName(String frameName) {
		this.frameName = frameName;
	}

	public String getBounds() {
		return bounds;
	}

	public void setBounds(String bounds) {
		this.bounds = bounds;
	}

	@Override
	public String toString() {
		return "FramSettingTable [id=" + id + ", bounds=" + bounds + "]";
	}

}

