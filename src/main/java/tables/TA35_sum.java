package tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ta35_sum")
public class TA35_sum {
	
	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "date")
	private String date;
	@Column(name = "open")
	private String open;
	@Column(name = "close")
	private String close;
	@Column(name = "high")
	private String high;
	@Column(name = "low")
	private String low;
	@Column(name = "f_up")
	private String f_up;
	@Column(name = "f_down")
	private String f_down;
	@Column(name = "index_up")
	private String index_up;
	@Column(name = "index_down")
	private String index_down;
	@Column(name = "op_avg")
	private String op_avg;
	@Column(name = "rando")
	private String rando;
	@Column(name = "tomorrow_future")
	private String tomorrow_future;
	@Column(name = "basket_up")
	private String basket_up;
	@Column(name = "basket_down")
	private String basket_down;
	@Column(name = "optimi_press")
	private String optimi_press;
	@Column(name = "pesimi_press")
	private String pesimi_press;
	@Column(name = "optimi_move")
	private double optimiMove;
	@Column(name = "pesimi_move")
	private double pesimiMove;
	@Column(name = "contract_bid_ask_counter")
	private double contractBidAskCounter;
	@Column(name = "equalMove")
	private double equalMove;
	

	// Constructor

	// Empty constructor
	public TA35_sum() {}

	public TA35_sum(String date, String open, String close, String high, String low, String f_up, String f_down,
			String index_up, String index_down, String op_avg, String rando, String tomorrowFuture, String basket_up,
			String basket_down, String optimi_press, String pesimi_press, double optimiMove, double pesimiMove, double contractBidAskCounter, double equalMove) {
		this.date = date;
		this.open = open;
		this.close = close;
		this.high = high;
		this.low = low;
		this.f_up = f_up;
		this.f_down = f_down;
		this.index_up = index_up;
		this.index_down = index_down;
		this.op_avg = op_avg;
		this.rando = rando;
		this.tomorrow_future = tomorrowFuture;
		this.basket_up = basket_up;
		this.basket_down = basket_down;
		this.optimi_press = optimi_press;
		this.pesimi_press = pesimi_press;
		this.optimiMove = optimiMove;
		this.pesimiMove = pesimiMove;
		this.contractBidAskCounter = contractBidAskCounter;
		this.equalMove = equalMove;
	}

	public double getContractBidAskCounter() {
		return contractBidAskCounter;
	}

	public void setContractBidAskCounter(double contractBidAskCounter) {
		this.contractBidAskCounter = contractBidAskCounter;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getClose() {
		return close;
	}

	public void setClose(String close) {
		this.close = close;
	}

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	public String getF_up() {
		return f_up;
	}

	public void setF_up(String f_up) {
		this.f_up = f_up;
	}

	public String getF_down() {
		return f_down;
	}

	public void setF_down(String f_down) {
		this.f_down = f_down;
	}

	public String getIndex_up() {
		return index_up;
	}

	public void setIndex_up(String index_up) {
		this.index_up = index_up;
	}

	public String getIndex_down() {
		return index_down;
	}

	public void setIndex_down(String index_down) {
		this.index_down = index_down;
	}

	public String getOp_avg() {
		return op_avg;
	}

	public void setOp_avg(String op_avg) {
		this.op_avg = op_avg;
	}

	public String getRando() {
		return rando;
	}

	public void setRando(String rando) {
		this.rando = rando;
	}

	public String getTomorrow_future() {
		return tomorrow_future;
	}

	public void setTomorrow_future(String tomorrow_future) {
		this.tomorrow_future = tomorrow_future;
	}

	public String getBasket_up() {
		return basket_up;
	}

	public void setBasket_up(String basket_up) {
		this.basket_up = basket_up;
	}

	public String getBasket_down() {
		return basket_down;
	}

	public void setBasket_down(String basket_down) {
		this.basket_down = basket_down;
	}

	public String getOptimi_press() {
		return optimi_press;
	}

	public void setOptimi_press(String optimi_press) {
		this.optimi_press = optimi_press;
	}

	public String getPesimi_press() {
		return pesimi_press;
	}

	public void setPesimi_press(String pesimi_press) {
		this.pesimi_press = pesimi_press;
	}
	
	public double getOptimiMove() {
		return optimiMove;
	}

	public void setOptimiMove(double optimiMove) {
		this.optimiMove = optimiMove;
	}

	public double getPesimiMove() {
		return pesimiMove;
	}

	public void setPesimiMove(double pesimiMove) {
		this.pesimiMove = pesimiMove;
	}
	
	public double getEqualMove() {
		return equalMove;
	}

	public void setEqualMove(double equalMove) {
		this.equalMove = equalMove;
	}

	@Override
	public String toString() {
		return "TA35_sum [id=" + id + ", date=" + date + ", open=" + open + ", close=" + close + ", high=" + high
				+ ", low=" + low + ", f_up=" + f_up + ", f_down=" + f_down + ", index_up=" + index_up + ", index_down="
				+ index_down + ", op_avg=" + op_avg + ", rando=" + rando + ", tomorrow_future=" + tomorrow_future
				+ ", basket_up=" + basket_up + ", basket_down=" + basket_down + ", optimi_press=" + optimi_press
				+ ", pesimi_press=" + pesimi_press + "]";
	}

}
