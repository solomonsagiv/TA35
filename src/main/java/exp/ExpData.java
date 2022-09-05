package exp;

import api.ApiObject;
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
	private double v2 = 0;
	private double v7 = 0;
	private double v8_de_corr = 0;

	ApiObject apiObject;
	Exp exp;

	public ExpData( ApiObject apiObject, Exp exp) {
		this.apiObject = apiObject;
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
		return baskets + (apiObject.getBasketUp() - apiObject.getBasketDown());
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

	public double getV2() {
		return v2;
	}

	public void setV2(double v2) {
		this.v2 = v2;
	}

	public double getV7() {
		return v7;
	}

	public void setV7(double v7) {
		this.v7 = v7;
	}

	public double getV8_de_corr() {
		return v8_de_corr;
	}

	public void setV8_de_corr(double v8_de_corr) {
		this.v8_de_corr = v8_de_corr;
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
