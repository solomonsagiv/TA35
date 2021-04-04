package dataBase.mySql.tables;

import java.time.LocalTime;

import org.json.JSONObject;

import dataBase.mySql.myBaseTables.MyStatusTable;
import dataBase.mySql.mySqlComps.MyColumnSql;
import dataBase.mySql.mySqlComps.MyLoadAbleColumn;
import dataBase.mySql.mySqlComps.MySqlColumnEnum;
import myJson.MyJson;
import options.Options;

public class StatusTable extends MyStatusTable {
	
	public StatusTable(String name) {
		super(name);
	}
	
	@Override
	public void initColumns() {
		
		Options optionsMonth = apiObject.getExpMonth().getOptions();
		Options optionsWeek = apiObject.getExpWeek().getOptions();
		
		addColumn(new MyColumnSql<String>(this, MySqlColumnEnum.NAME) {
			@Override
			public String getObject() {
				return "ta35";
			}
		});
		addColumn(new MyColumnSql<String>(this, MySqlColumnEnum.time) {
			@Override
			public String getObject() {
				return LocalTime.now().toString();
			}
		});
		addColumn(new MyColumnSql<Double>(this, MySqlColumnEnum.conMonth) {
			@Override
			public Double getObject() {
				return optionsMonth.getContract();
			}
		});
		addColumn(new MyColumnSql<Double>(this, MySqlColumnEnum.ind) {
			@Override
			public Double getObject() {
				return apiObject.getIndex();
			}
		});
		addColumn(new MyLoadAbleColumn<Integer>(this, MySqlColumnEnum.conUp) {
			@Override
			public Integer getObject() {
				return apiObject.getConUp();
			}

			@Override
			public void setLoadedObject(Integer object) {
				apiObject.setConUp(object);
			}
			
			@Override
			public Integer getResetObject() {
				return 0;
			}
		});
		addColumn(new MyLoadAbleColumn<Integer>(this, MySqlColumnEnum.conDown) {
			@Override
			public Integer getObject() {
				return apiObject.getConDown();
			}

			@Override
			public void setLoadedObject(Integer object) {
				apiObject.setConDown(object);
			}

			@Override
			public Integer getResetObject() {
				return 0;
			}

		});
		addColumn(new MyLoadAbleColumn<Integer>(this, MySqlColumnEnum.indUp) {
			@Override
			public Integer getObject() {
				return apiObject.getIndUp();
			}

			@Override
			public void setLoadedObject(Integer object) {
				apiObject.setIndUp(object);
			}

			@Override
			public Integer getResetObject() {
				return 0;
			}

		});
		addColumn(new MyLoadAbleColumn<Integer>(this, MySqlColumnEnum.indDown) {
			@Override
			public Integer getObject() {
				return apiObject.getIndDown();
			}

			@Override
			public void setLoadedObject(Integer object) {
				apiObject.setIndDown(object);
			}

			@Override
			public Integer getResetObject() {
				return 0;
			}
		});
		addColumn(new MyColumnSql<Double>(this, MySqlColumnEnum.base) {
			@Override
			public Double getObject() {
				return apiObject.getBase();
			}
		});
		addColumn(new MyColumnSql<Double>(this, MySqlColumnEnum.open) {
			@Override
			public Double getObject() {
				return apiObject.getOpen();
			}
		});
		addColumn(new MyColumnSql<Double>(this, MySqlColumnEnum.high) {
			@Override
			public Double getObject() {
				return apiObject.getHigh();
			}
		});
		addColumn(new MyColumnSql<Double>(this, MySqlColumnEnum.low) {
			@Override
			public Double getObject() {
				return apiObject.getLow();
			}
		});

		addColumn(new MyLoadAbleColumn<Double>(this, MySqlColumnEnum.optimiMove) {

			@Override
			public void setLoadedObject(Double object) {
				apiObject.setOptimiMoveFromOutSide(object);
			}

			@Override
			public Double getResetObject() {
				return 0.0;
			}

			@Override
			public Double getObject() {
				return apiObject.getOptimiLiveMove();
			}
		});

		addColumn(new MyLoadAbleColumn<Double>(this, MySqlColumnEnum.pesimiMove) {
			@Override
			public Double getObject() {
				return apiObject.getPesimiLiveMove();
			}

			@Override
			public void setLoadedObject(Double object) {
				apiObject.setPesimiMoveFromOutSide(object);
			}

			@Override
			public Double getResetObject() {
				return 0.0;
			}
		});

		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.optionsWeek) {

			@Override
			public void setLoadedObject(String object) {
				System.out.println(object);
				optionsWeek.loadFromJson(new MyJson(object));
			}

			@Override
			public String getResetObject() {
				return new JSONObject().toString();
			}

			@Override
			public String getObject() {
				return optionsWeek.getOptionsWithDataAsJson().toString();
			}
		});

		addColumn(new MyLoadAbleColumn<String>(this, MySqlColumnEnum.optionsMonth) {

			@Override
			public void setLoadedObject(String object) {
				optionsMonth.loadFromJson(new MyJson(object));
			}

			@Override
			public String getResetObject() {
				return new JSONObject().toString();
			}

			@Override
			public String getObject() {
				return optionsMonth.getOptionsWithDataAsJson().toString();
			}
		});

		addColumn(new MyLoadAbleColumn<Integer>(this, MySqlColumnEnum.basketUp) {

			@Override
			public void setLoadedObject(Integer object) {
				apiObject.setBasketUp(object);
			}

			@Override
			public Integer getResetObject() {
				return 0;
			}

			@Override
			public Integer getObject() {
				return apiObject.getBasketUp();
			}
		});

		addColumn(new MyLoadAbleColumn<Integer>(this, MySqlColumnEnum.basketDown) {

			@Override
			public void setLoadedObject(Integer object) {
				apiObject.setBasketDown(object);
			}

			@Override
			public Integer getResetObject() {
				return 0;
			}

			@Override
			public Integer getObject() {
				return apiObject.getBasketDown();
			}
		});

		addColumn(new MyColumnSql<Double>(this, MySqlColumnEnum.indBid) {
			@Override
			public Double getObject() {
				return apiObject.getIndex_bid();
			}
		});
		addColumn(new MyColumnSql<Double>(this, MySqlColumnEnum.indAsk) {
			@Override
			public Double getObject() {
				return apiObject.getIndex_ask();
			}
		});

//		addColumn(new MyLoadAbleColumn<Double>(this, MySqlColumnEnum.expStart) {
//
//			@Override
//			public void setLoadedObject(Double object) {
//				apiObject.setStartExp(object);
//			}
//
//			@Override
//			public Double getResetObject() {
//				return apiObject.getStartExp();
//			}
//
//			@Override
//			public Double getObject() {
//				return apiObject.getStartExp();
//			}
//		});

//		addColumn(new MyLoadAbleColumn<Double>(this, MySqlColumnEnum.expWeekStart) {
//
//			@Override
//			public void setLoadedObject(Double object) {
//				apiObject.setStartWeekExp(object);
//			}
//
//			@Override
//			public Double getResetObject() {
//				return apiObject.getStartWeekExp();
//			}
//
//			@Override
//			public Double getObject() {
//				return apiObject.getStartWeekExp();
//			}
//		});

		addColumn(new MyLoadAbleColumn<Double>(this, MySqlColumnEnum.equalMove) {
			
			@Override
			public void setLoadedObject(Double object) {
				apiObject.setEqualMove(object);
			}

			@Override
			public Double getResetObject() {
				return 0.0;
			}
			
			@Override
			public Double getObject() {
				return apiObject.getEqualLiveMove();
			}
		});
		addColumn(new MyLoadAbleColumn<Double>(this, MySqlColumnEnum.indDelta) {

			@Override
			public void setLoadedObject(Double object) {
				apiObject.getStocksHandler().setDelta(object);
			}

			@Override
			public Double getResetObject() {
				return 0.0;
			}

			@Override
			public Double getObject() {
				return apiObject.getStocksHandler().getDelta();
			}
		});
	}
}
