package tables;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.simple.JSONArray;

@Entity
@Table(name = "arrays")
public class ArraysTable {

	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "time")
	private String time = LocalTime.now().toString();
	@Column(name = "conBdCounterList")
	private String conBdCounterList = new JSONArray().toString();
	@Column(name = "indexList")
	private String indexList = new JSONArray().toString();
	@Column(name = "opList")
	private String opList = new JSONArray().toString();
	
	public String getOpList() {
		return opList;
	}

	public void setOpList(String opList) {
		this.opList = opList;
	}

	public ArraysTable(int id, String time, String conBdCounterList, String indexList, String opList) {
		this.id = id;
		this.time = time;
		this.conBdCounterList = conBdCounterList;
		this.indexList = indexList;
		this.opList = opList;
	}

	public ArraysTable() {}

	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getConBdCounterList() {
		return conBdCounterList;
	}

	public void setConBdCounterList(String conBdCounterList) {
		this.conBdCounterList = conBdCounterList;
	}

	public String getIndexList() {
		return indexList;
	}

	public void setIndexList(String indexList) {
		this.indexList = indexList;
	}
	
}
