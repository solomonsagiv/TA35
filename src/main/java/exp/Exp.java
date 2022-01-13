package exp;

import api.ApiObject;
import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;
import options.Options;

public abstract class Exp implements IJsonData {

	public static final String WEEK_SYMBOL = "W";
	public static final String MONTH_SYMBOL = "M";

	protected ApiObject apiObject;
	private Options options;
	private ExpData expData;
	private String exp_name;
	private String symbol = "";

	private int futureStartStrike = 0;
	private int futureEndStrike = 0;

	private double yesterday_op_avg_60 = 0;
	private double yesterday_op_avg_240 = 0;
	private double op_avg_60 = 0;
	private double op_avg15 = 0;
	private double op_avg = 0;

	public Exp(ApiObject apiObject, String exp_name) {
		this.apiObject = apiObject;
		this.exp_name = exp_name;
		options = new Options(apiObject);
		expData = new ExpData(apiObject, this);
		this.symbol = exp_name.toLowerCase().contains("w") ? WEEK_SYMBOL : MONTH_SYMBOL;
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

	public double getOp_avg_60() {
		return op_avg_60;
	}

	public double getOp_avg() {
		return op_avg;
	}

	public void setOp_avg_60(double op_avg_60) {
		this.op_avg_60 = op_avg_60;
	}

	public void setOp_avg(double op_avg) {
		this.op_avg = op_avg;
	}

	public String getExp_name() {
		return exp_name;
	}

	public void setExp_name(String exp_name) {
		this.exp_name = exp_name;
	}

	public void setOp_avg15(double op_avg15) {
		this.op_avg15 = op_avg15;
	}

	public double getOp_avg15() {
		return op_avg15;
	}

	public double getYesterday_op_avg_60() {
		return yesterday_op_avg_60;
	}

	public void setYesterday_op_avg_60(double yesterday_op_avg_60) {
		this.yesterday_op_avg_60 = yesterday_op_avg_60;
	}

	public double getYesterday_op_avg_240() {
		return yesterday_op_avg_240;
	}

	public void setYesterday_op_avg_240(double yesterday_op_avg_240) {
		this.yesterday_op_avg_240 = yesterday_op_avg_240;
	}

	public String getSymbol() {
		return symbol;
	}

}
