package tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ta35data")
public class TA35Data {
	
	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "date")
	private String date;
	@Column(name = "time")
	private String time;
	@Column(name = "fut")
	private double future;
	@Column(name = "ind")
	private double index;
	@Column(name = "futUp")
	private int future_up;
	@Column(name = "futDown")
	private int future_down;
	@Column(name = "indUp")
	private int index_up;
	@Column(name = "indDown")
	private int index_down;
	@Column(name = "optimiCounter")
	private int optimi_counter;
	@Column(name = "pesimiCounter")
	private int pesimi_counter;
	@Column(name = "basketUp")
	private int basket_up;
	@Column(name = "basketDown")
	private int basket_down;
	@Column(name = "optimiMove")
	private double optimiMove;
	@Column(name = "pesimiMove")
	private double pesimiMove;
	@Column(name = "futBidAskCounter")
	private int futureBidAskCounter;
	@Column(name = "indBidAskCounter")
	private int indexBidAskCounter;
	@Column(name = "opAvg")
	private double opAvg;
	@Column(name = "options")
	private String options;
	@Column(name = "base")
	private double base;
	@Column(name = "indBid")
	private double indexBid;
	@Column(name = "indAsk")
	private double indexAsk;

	// Empty constructor
	public TA35Data() {
	}
	
	public TA35Data(String date, String time, double future, double index, int future_up, int future_down, int index_up,
			int index_down, int optimi_counter, int pesimi_counter, int basket_up, int basket_down, double optimiMove,
			double pesimiMove, int futureBidAskCounter, int indexBidAskCounter, double opAvg, String options,
			double base, double indexBid, double indexAsk) {
		this.date = date;
		this.time = time;
		this.future = future;
		this.index = index;
		this.future_up = future_up;
		this.future_down = future_down;
		this.index_up = index_up;
		this.index_down = index_down;
		this.optimi_counter = optimi_counter;
		this.pesimi_counter = pesimi_counter;
		this.basket_up = basket_up;
		this.basket_down = basket_down;
		this.optimiMove = optimiMove;
		this.pesimiMove = pesimiMove;
		this.futureBidAskCounter = futureBidAskCounter;
		this.indexBidAskCounter = indexBidAskCounter;
		this.opAvg = opAvg;
		this.options = options;
		this.base = base;
		this.indexBid = indexBid;
		this.indexAsk = indexAsk;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getBase() {
		return base;
	}

	public void setBase(double base) {
		this.base = base;
	}

	public double getIndexBid() {
		return indexBid;
	}

	public void setIndexBid(double indexBid) {
		this.indexBid = indexBid;
	}

	public double getIndexAsk() {
		return indexAsk;
	}

	public void setIndexAsk(double indexAsk) {
		this.indexAsk = indexAsk;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public double getFuture() {
		return future;
	}

	public void setFuture(double future) {
		this.future = future;
	}

	public double getIndex() {
		return index;
	}

	public void setIndex(double index) {
		this.index = index;
	}

	public int getFuture_up() {
		return future_up;
	}

	public void setFuture_up(int future_up) {
		this.future_up = future_up;
	}

	public int getFuture_down() {
		return future_down;
	}

	public void setFuture_down(int future_down) {
		this.future_down = future_down;
	}

	public int getIndex_up() {
		return index_up;
	}

	public void setIndex_up(int index_up) {
		this.index_up = index_up;
	}

	public int getIndex_down() {
		return index_down;
	}

	public void setIndex_down(int index_down) {
		this.index_down = index_down;
	}

	public int getOptimi_counter() {
		return optimi_counter;
	}

	public void setOptimi_counter(int optimi_counter) {
		this.optimi_counter = optimi_counter;
	}

	public int getPesimi_counter() {
		return pesimi_counter;
	}

	public void setPesimi_counter(int pesimi_counter) {
		this.pesimi_counter = pesimi_counter;
	}

	public int getBasket_up() {
		return basket_up;
	}

	public void setBasket_up(int basket_up) {
		this.basket_up = basket_up;
	}

	public int getBasket_down() {
		return basket_down;
	}

	public void setBasket_down(int basket_down) {
		this.basket_down = basket_down;
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

	public int getFutureBidAskCounter() {
		return futureBidAskCounter;
	}

	public void setFutureBidAskCounter(int futureBidAskCounter) {
		this.futureBidAskCounter = futureBidAskCounter;
	}

	public int getIndexBidAskCounter() {
		return indexBidAskCounter;
	}

	public void setIndexBidAskCounter(int indexBidAskCounter) {
		this.indexBidAskCounter = indexBidAskCounter;
	}

	public double getOpAvg() {
		return opAvg;
	}

	public void setOpAvg(double opAvg) {
		this.opAvg = opAvg;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	@Override
	public String toString() {
		return "TA35Daily [id=" + id + ", time=" + time + ", future=" + future + ", index=" + index + ", future_up="
				+ future_up + ", future_down=" + future_down + ", index_up=" + index_up + ", index_down=" + index_down
				+ ", optimi_counter=" + optimi_counter + ", pesimi_counter=" + pesimi_counter + ", basket_up="
				+ basket_up + ", basket_down=" + basket_down + "]";
	}

}
