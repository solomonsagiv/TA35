package exp;

import api.BASE_CLIENT_OBJECT;
import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;
import options.Options;

public abstract class Exp implements IJsonData {

	public static final String WEEK_SYMBOL = "W";
	public static final String MONTH_SYMBOL = "M";

	protected BASE_CLIENT_OBJECT client;
	private Options options;
	private ExpData expData;
	private String exp_name;
	private String symbol = "";

	private int futureStartStrike = 0;
	private int futureEndStrike = 0;

	private double continue_op_avg_240 = 0;
	private double op_avg_60 = 0;
	private double op_avg_15 = 0;
	private double op_avg = 0;

	private int optimi_count = 0;
	private int pesimi_count = 0;

	private int roll_optimi_count = 0;
	private int roll_pesimi_count = 0;


	public Exp(BASE_CLIENT_OBJECT client, String exp_name) {
		this.client = client;
		this.exp_name = exp_name;
		options = new Options(client);
		expData = new ExpData(client, this);
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
		// Check for zero crossing if client is TA35
		if (client instanceof api.TA35) {
			((api.TA35) client).checkOpAvg60Cross(op_avg_60);
		}
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

	public double getContinue_op_avg_240() {
		return continue_op_avg_240;
	}

	public void setContinue_op_avg_240(double continue_op_avg_240) {
		this.continue_op_avg_240 = continue_op_avg_240;
	}

	public String getSymbol() {
		return symbol;
	}

	public double getOp_avg_15() {
		return op_avg_15;
	}

	public void setOp_avg_15(double op_avg_15) {
		// Check for zero crossing if client is TA35
		if (client instanceof api.TA35) {
			((api.TA35) client).checkOpAvg15Cross(op_avg_15);
		}
		this.op_avg_15 = op_avg_15;
	}

	public int getOptimi_count() {
		return optimi_count;
	}

	public void setOptimi_count(int optimi_count) {
		this.optimi_count = optimi_count;
	}

	public int getPesimi_count() {
		return pesimi_count;
	}

	public void setPesimi_count(int pesimi_count) {
		this.pesimi_count = pesimi_count;
	}

	public int getRoll_optimi_count() {
		return roll_optimi_count;
	}

	public void setRoll_optimi_count(int roll_optimi_count) {
		this.roll_optimi_count = roll_optimi_count;
	}

	public int getRoll_pesimi_count() {
		return roll_pesimi_count;
	}

	public void setRoll_pesimi_count(int roll_pesimi_count) {
		this.roll_pesimi_count = roll_pesimi_count;
	}

}
