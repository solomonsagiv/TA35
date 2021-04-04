package exp;

import api.ApiObject;
import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;
import options.Options;

public abstract class Exp implements IJsonData {

	protected ApiObject apiObject;
	private Options options;
	private ExpData expData;
	
	private int futureStartStrike = 0;
	private int futureEndStrike = 0;

	public Exp(ApiObject apiObject) {
		this.apiObject = apiObject;
		options = new Options(apiObject);
		expData = new ExpData(apiObject, this);
	}

	public Options getOptions() {
		return options;
	}

	public ExpData getExpData() {
		return expData;
	}
	
	@Override
	public MyJson getAsJson() {
		MyJson json = new MyJson();
		json.put(JsonStrings.expData, expData.getAsJson());
		json.put(JsonStrings.options, options.getAsJson());
		return json;
	}
	
	@Override
	public void loadFromJson(MyJson json) {
		getExpData().loadFromJson(new MyJson(json.getJSONObject(JsonStrings.expData)));
		getOptions().loadFromJson(new MyJson(json.getJSONObject(JsonStrings.options)));
	}
	
	@Override
	public MyJson getResetJson() {
		MyJson json = new MyJson();
		json.put(JsonStrings.expData, getExpData().getResetJson());
		return json;
	}
	
	public MyJson getFullResetJson() {
		return new MyJson();
	}

	public ApiObject getApiObject() {
		return apiObject;
	}

	public void setApiObject(ApiObject apiObject) {
		this.apiObject = apiObject;
	}

	public int getFutureStartStrike() {
		return futureStartStrike;
	}

	public void setFutureStartStrike(int futureStartStrike) {
		this.futureStartStrike = futureStartStrike;
	}

	public int getFutureEndStrike() {
		return futureEndStrike;
	}

	public void setFutureEndStrike(int futureEndStrike) {
		this.futureEndStrike = futureEndStrike;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public void setExpData(ExpData expData) {
		this.expData = expData;
	}
	
	
	
}
