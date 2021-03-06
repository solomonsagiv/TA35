package dataBase.mySql.mySqlComps;

public enum MySqlColumnEnum {
	
    ID("id", MySqlDataTypeEnum.INT),
    NAME("name", MySqlDataTypeEnum.STRING),
    date("date", MySqlDataTypeEnum.STRING),
    open("open", MySqlDataTypeEnum.DOUBLE),
    high("high", MySqlDataTypeEnum.DOUBLE),
    low("low", MySqlDataTypeEnum.DOUBLE),
    close("close", MySqlDataTypeEnum.DOUBLE),
    rando("rando", MySqlDataTypeEnum.STRING),
    con_up("con_up", MySqlDataTypeEnum.INT),
    tomorrow_future("tomorrow_future", MySqlDataTypeEnum.INT),
    con_down("con_down", MySqlDataTypeEnum.INT),
    conDown("conDown", MySqlDataTypeEnum.INT),
    conUp("conUp", MySqlDataTypeEnum.INT),
    indUp("indUp", MySqlDataTypeEnum.INT),
    opAvg("opAvg", MySqlDataTypeEnum.DOUBLE),
    indDown("indDown", MySqlDataTypeEnum.INT),
    futUp("futUp", MySqlDataTypeEnum.INT),
    futDown("futDown", MySqlDataTypeEnum.INT),
    opAvgWeek("opAvgWeek", MySqlDataTypeEnum.DOUBLE),
    opAvgMonth("opAvgMonth", MySqlDataTypeEnum.DOUBLE),
    op_avg("op_avg", MySqlDataTypeEnum.DOUBLE),
    futBidAskCounter("futBidAskCounter", MySqlDataTypeEnum.INT),
    base("base", MySqlDataTypeEnum.DOUBLE),
    options("options", MySqlDataTypeEnum.STRING),
    contract_bid_ask_counter("contract_bid_ask_counter", MySqlDataTypeEnum.INT),
    conBdCounter("conBdCounter", MySqlDataTypeEnum.INT),
    EXP_NAME("exp_name", MySqlDataTypeEnum.STRING),
    SPX_STOCKS("spx", MySqlDataTypeEnum.STRING),
    time("time", MySqlDataTypeEnum.STRING),
    conMonth("con", MySqlDataTypeEnum.DOUBLE),
    conWeek("conWeek", MySqlDataTypeEnum.DOUBLE),
    deltaWeek("deltaWeek", MySqlDataTypeEnum.DOUBLE),
    deltaMonth("deltaMonth", MySqlDataTypeEnum.DOUBLE),
    fut("fut", MySqlDataTypeEnum.DOUBLE),
    con("con", MySqlDataTypeEnum.DOUBLE),
    ind("ind", MySqlDataTypeEnum.DOUBLE),
    indBid("indBid", MySqlDataTypeEnum.DOUBLE),
    indAsk("indAsk", MySqlDataTypeEnum.DOUBLE),
    indBidAskCounter("indBidAskCounter", MySqlDataTypeEnum.INT),
    CON_WEEK("conWeek", MySqlDataTypeEnum.DOUBLE),
    CON_WEEK_BID("conWeekBid", MySqlDataTypeEnum.DOUBLE),
    CON_WEEK_ASK("conWeekAsk", MySqlDataTypeEnum.DOUBLE),
    CON_WEEK_BID_ASK_COUNTER_LIST("conWeekBidAskCounterList",MySqlDataTypeEnum.STRING ),
    CON_MONTH("conMonth", MySqlDataTypeEnum.DOUBLE),
    CON_MONTH_BID_ASK_COUNTER_LIST("conMonthBidAskCounterList",MySqlDataTypeEnum.STRING ),
    CON_MONTH_BID("conMonthBid", MySqlDataTypeEnum.DOUBLE),
    CON_MONTH_ASK("conMonthAsk", MySqlDataTypeEnum.DOUBLE),
    CON_QUARTER("conQuarter", MySqlDataTypeEnum.DOUBLE),
    CON_QUARTER_BID("conQuarterBid", MySqlDataTypeEnum.DOUBLE),
    CON_QUARTER_ASK("conQuarterAsk", MySqlDataTypeEnum.DOUBLE),
    CON_QUARTER_FAR("conQuarterFar", MySqlDataTypeEnum.DOUBLE),
    CON_QUARTER_FAR_BID("conQuarterFarBid", MySqlDataTypeEnum.DOUBLE),
    CON_QUARTER_FAR_ASK("conQuarterFarAsk", MySqlDataTypeEnum.DOUBLE),
    SEC_TYPE("secType", MySqlDataTypeEnum.STRING),
    CURRENCY("currency", MySqlDataTypeEnum.STRING),
    EXCHANGE("exchange", MySqlDataTypeEnum.STRING),
    TRADING_CLASS("tradingClass", MySqlDataTypeEnum.STRING),
    MULTIPLIER("multiplier", MySqlDataTypeEnum.STRING),
    PRIMARY_EXCHANGE("primaryExchange", MySqlDataTypeEnum.STRING),
    SYMBOL("symbol", MySqlDataTypeEnum.STRING),
    includExpired("includExpired", MySqlDataTypeEnum.STRING),
    IND_BID_ASK_COUNTER_LIST("indexBidAskCounterList", MySqlDataTypeEnum.STRING),
    QUARTER_FUT_BID_ASK_COUNTER_LIST("quarterFutBidAskCounterList", MySqlDataTypeEnum.STRING),
    QUARTER_FAR_FUT_BID_ASK_COUNTER_LIST("quarterFarFutBidAskCounterList", MySqlDataTypeEnum.STRING),
    LAST_TRADING_DATE_OR_CONTRACT_MONTH("lastTradingDayOrContractMonth", MySqlDataTypeEnum.STRING),
    E1("e1", MySqlDataTypeEnum.DOUBLE),
    E1_BID("e1_bid", MySqlDataTypeEnum.DOUBLE),
    E1_ASK("e1_ask", MySqlDataTypeEnum.DOUBLE),
    E2("e2", MySqlDataTypeEnum.DOUBLE),
    E2_BID("e2_bid", MySqlDataTypeEnum.DOUBLE),
    E2_ASK("e2_ask", MySqlDataTypeEnum.DOUBLE),
    ROLL("roll", MySqlDataTypeEnum.DOUBLE),
    ROLL_AVG("rollAvg", MySqlDataTypeEnum.DOUBLE), 
    CON_BD_COUNTER_LIST("conBdCounterList", MySqlDataTypeEnum.STRING),
    optimiCounter("optimiCounter", MySqlDataTypeEnum.INT),
    pesimiCounter("pesimiCounter", MySqlDataTypeEnum.INT),
    OptimiMove("OptimiMove", MySqlDataTypeEnum.DOUBLE),
    optimiMove("optimiMove", MySqlDataTypeEnum.DOUBLE),
    pesimiMove("pesimiMove", MySqlDataTypeEnum.DOUBLE),
    basketUp("basketUp", MySqlDataTypeEnum.INT),
    basketDown("basketDown", MySqlDataTypeEnum.INT),
	basket_up("basket_up", MySqlDataTypeEnum.STRING),
	basket_down("basket_down", MySqlDataTypeEnum.STRING),
	expStart("expStart", MySqlDataTypeEnum.DOUBLE),
	equalMove("equalMove", MySqlDataTypeEnum.DOUBLE),
	F_UP("f_up", MySqlDataTypeEnum.INT),
	F_DOWN("f_down", MySqlDataTypeEnum.INT),
	INDEX_UP("index_up", MySqlDataTypeEnum.INT),
	INDEX_DOWN("index_down", MySqlDataTypeEnum.INT),
	expWeekStart("expWeekStart", MySqlDataTypeEnum.DOUBLE),
	conBidAskCounterWeekList("conBidAskCounterWeekList", MySqlDataTypeEnum.STRING),
	conBidAskCounterMonthList("conBidAskCounterMonthList", MySqlDataTypeEnum.STRING),
	opWeekList("opWeekList", MySqlDataTypeEnum.STRING),
	opMonthList("opMonthList", MySqlDataTypeEnum.STRING),
	indexList("indexList", MySqlDataTypeEnum.STRING),
	deltaWeekList("deltaWeekList", MySqlDataTypeEnum.STRING),
	deltaMonthList("deltaMonthList", MySqlDataTypeEnum.STRING),
	opList("opList", MySqlDataTypeEnum.STRING),
	optionsWeek("optionsWeek", MySqlDataTypeEnum.STRING),
	optionsMonth("optionsMonth", MySqlDataTypeEnum.STRING),
	data("data", MySqlDataTypeEnum.STRING),
	indDeltaList("indDeltaList", MySqlDataTypeEnum.STRING),
	indDeltaNoBasketsList("indDeltaNoBasketsList", MySqlDataTypeEnum.STRING),
	indBasketsList("indBasketsList", MySqlDataTypeEnum.STRING),
	indDelta("indDelta", MySqlDataTypeEnum.DOUBLE);
	
	private final String name;
    private final MySqlDataTypeEnum dataType;

    MySqlColumnEnum(String name, MySqlDataTypeEnum dataType ) {
        this.name = name;
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public MySqlDataTypeEnum getDataType() {
        return dataType;
    }


}
