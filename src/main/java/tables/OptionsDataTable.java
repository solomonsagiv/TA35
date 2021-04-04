package tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "options_data")
public class OptionsDataTable {

	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "option_name")
	private String option;
	@Column(name = "bid_ask_counter")
	private int bidAskCounter;

	// Empty constructor
	public OptionsDataTable() {}

	// Constructor
	public OptionsDataTable(String option, int bidAskCounter) {
		this.option = option;
		this.bidAskCounter = bidAskCounter;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public int getBidAskCounter() {
		return bidAskCounter;
	}

	public void setBidAskCounter(int bidAskCounter) {
		this.bidAskCounter = bidAskCounter;
	}

	@Override
	public String toString() {
		return "OptionsDataTable [id=" + id + ", option=" + option + ", bidAskCounter=" + bidAskCounter + "]";
	}

}
