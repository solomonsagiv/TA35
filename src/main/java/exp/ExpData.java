package exp;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;

public class ExpData implements IJsonData {

	private double start = 0;
	private int baskets = 0;
	private double v4 = 0;
	private double v8 = 0;
	private double v6 = 0;
	private double v5 = 0;
	private double v9 = 0;

	TA35 client;
	Exp exp;

	public ExpData(BASE_CLIENT_OBJECT client, Exp exp) {
		this.client = (TA35) client;
		this.exp = exp;
	}

	public void setStart(double start) {
		this.start = start;
	}
	
	public double getStart() {
		return start;
	}
	
	public void setBaskets(int baskets) {
		this.baskets = baskets;
	}

	public int getTotalBaskets() {
		return baskets + (client.getBasketFinder_by_stocks().getBasket_up() - client.getBasketFinder_by_stocks().getBasket_down());
	}

	public double getV4() {
		return v4;
	}

	public void setV4(double v4) {
		this.v4 = v4;
	}

	public double getV8() {
		return v8;
	}

	public void setV8(double v8) {
		this.v8 = v8;
	}

	public double getV6() {
		return v6;
	}

	public void setV6(double v6) {
		this.v6 = v6;
	}

	public double getV5() {
		return v5;
	}

	public void setV5(double v5) {
		this.v5 = v5;
	}

	public double getV9() {
		return v9;
	}

	public void setV9(double v9) {
		this.v9 = v9;
	}

	@Override
	public MyJson getAsJson() {
		MyJson json = new MyJson();
		json.put(JsonStrings.expStart, start);
		json.put(JsonStrings.baskets, baskets);
		return json;
	}

	@Override
	public void loadFromJson(MyJson json) {
		setStart(json.getDouble(JsonStrings.expStart));
		setBaskets(json.getInt(JsonStrings.baskets));
	}
	
	@Override
	public MyJson getResetJson() {
		MyJson json = new MyJson();
		json.put(JsonStrings.expStart, start);
		json.put(JsonStrings.baskets, getTotalBaskets());
		return json;
	}
	
	@Override
	public MyJson getFullResetJson() {
		return getResetJson();
	}

}
