package exp;

import api.ApiObject;
import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;

public class ExpData implements IJsonData {

	private double start = 0;
	private double delta = 0;
	private int baskets = 0;
	private double indDelta = 0;

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
	
	public void setDelta(double delta) {
		this.delta = delta;
	}

	public void setIndDelta(double indDelta) {
		this.indDelta = indDelta;
	}
	
	public double getTotalDelta() {
		return delta + exp.getOptions().getDelta();
	}
	
	public double getTotalIndDelta() {
		return indDelta + apiObject.getStocksHandler().getDelta();
	}
	
	public int getTotalBaskets() {
		return baskets + (apiObject.getBasketUp() - apiObject.getBasketDown());
	}
	
	@Override
	public MyJson getAsJson() {
		MyJson json = new MyJson();
		json.put(JsonStrings.expStart, start);
		json.put(JsonStrings.delta, delta);
		json.put(JsonStrings.baskets, baskets);
		json.put(JsonStrings.indDelta, indDelta);
		return json;
	}

	@Override
	public void loadFromJson(MyJson json) {
		setStart(json.getDouble(JsonStrings.expStart));
		setDelta(json.getDouble(JsonStrings.delta));
		setBaskets(json.getInt(JsonStrings.baskets));
		setIndDelta(json.getDouble(JsonStrings.indDelta));
	}
	
	@Override
	public MyJson getResetJson() {
		MyJson json = new MyJson();
		json.put(JsonStrings.expStart, start);
		json.put(JsonStrings.delta, getTotalDelta());
		json.put(JsonStrings.baskets, getTotalBaskets());
		json.put(JsonStrings.indDelta, getTotalIndDelta());
		return json;
	}
	
	@Override
	public MyJson getFullResetJson() {
		return getResetJson();
	}

}
