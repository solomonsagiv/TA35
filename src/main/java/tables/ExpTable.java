package tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "exp_races")
public class ExpTable {

	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "exp_future_races")
	private int future_races;
	@Column(name = "exp_index_races")
	private int index_races;
	@Column(name = "exp_start_exp")
	private double start_exp;
	@Column(name = "exp_optimi_timer")
	private int optimi_timer;
	@Column(name = "exp_pesimi_timer")
	private int pesimi_timer;
	@Column(name = "exp_basket_up")
	private int basket_up;
	@Column(name = "exp_basket_down")
	private int basket_down;

	public ExpTable(int future_races, int index_races, double start_exp, int optimi_timer, int pesimi_timer,
			int basket_up, int basket_down) {
		this.future_races = future_races;
		this.index_races = index_races;
		this.start_exp = start_exp;
		this.optimi_timer = optimi_timer;
		this.pesimi_timer = pesimi_timer;
		this.basket_up = basket_up;
		this.basket_down = basket_down;
	}

	// Empty constructor
	public ExpTable() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFuture_races() {
		return future_races;
	}

	public void setFuture_races(int future_races) {
		this.future_races = future_races;
	}

	public int getIndex_races() {
		return index_races;
	}

	public void setIndex_races(int index_races) {
		this.index_races = index_races;
	}

	public double getStart_exp() {
		return start_exp;
	}

	public void setStart_exp(double start_exp) {
		this.start_exp = start_exp;
	}
	
	public int getOptimi_timer() {
		return optimi_timer;
	}

	public void setOptimi_timer(int optimi_timer) {
		this.optimi_timer = optimi_timer;
	}

	public int getPesimi_timer() {
		return pesimi_timer;
	}

	public void setPesimi_timer(int pesimi_timer) {
		this.pesimi_timer = pesimi_timer;
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

	@Override
	public String toString() {
		return "ExpTable [id=" + id + ", future_races=" + future_races + ", index_races=" + index_races + ", start_exp="
				+ start_exp + ", optimi_timer=" + optimi_timer + ", pesimi_timer=" + pesimi_timer + ", basket_up="
				+ basket_up + ", basket_down=" + basket_down + "]";
	}
	

}
