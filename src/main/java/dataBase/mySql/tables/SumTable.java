package dataBase.mySql.tables;

import counter.WindowTA35;
import dataBase.mySql.myBaseTables.MySumTable;
import dataBase.mySql.mySqlComps.MyColumnSql;
import dataBase.mySql.mySqlComps.MySqlColumnEnum;
import options.Options;

import java.time.LocalDate;

public class SumTable extends MySumTable {

	public SumTable(String name) {
		super(name);
	}
	
	@Override
	public void initColumns() {
		
		
		Options optionsMonth = apiObject.getExpMonth().getOptions();
		Options optionsWeek = apiObject.getExpWeek().getOptions();
		
		addColumn(new MyColumnSql<String>(this, MySqlColumnEnum.date) {
			@Override
			public String getObject() {
				return LocalDate.now().toString();
			}
		});
		addColumn(new MyColumnSql<Double>(this, MySqlColumnEnum.open) {
			@Override
			public Double getObject() {
				return apiObject.getOpen();
			}
		});
		addColumn(new MyColumnSql<Double>(this, MySqlColumnEnum.close) {
			@Override
			public Double getObject() {
				return apiObject.getIndex();
			}
		});
		addColumn(new MyColumnSql<Double>(this,  MySqlColumnEnum.high) {
			@Override
			public Double getObject() {
				return apiObject.getHigh();
			}
		});
		addColumn(new MyColumnSql<Double>(this,  MySqlColumnEnum.low) {
			@Override
			public Double getObject() {
				return apiObject.getLow();
			}
		});
		addColumn(new MyColumnSql<Integer>(this,  MySqlColumnEnum.conUp) {
			@Override
			public Integer getObject() {
				return apiObject.getConUp();
			}
		});
		addColumn(new MyColumnSql<Integer>(this,  MySqlColumnEnum.conDown) {
			@Override
			public Integer getObject() {
				return apiObject.getConDown();
			}
		});
		addColumn(new MyColumnSql<Integer>(this,  MySqlColumnEnum.indUp) {
			@Override
			public Integer getObject() {
				return apiObject.getIndUp();
			}
		});
		addColumn(new MyColumnSql<Integer>(this,  MySqlColumnEnum.indDown) {
			@Override
			public Integer getObject() {
				return apiObject.getIndDown();
			}
		});
		addColumn(new MyColumnSql<String>(this, MySqlColumnEnum.rando) {

			@Override
			public String getObject() {
				try {
					return WindowTA35.rando.getText();
				} catch (Exception e) {
					return String.valueOf(0);
				}
			}
		});
		addColumn(new MyColumnSql<Double>(this,  MySqlColumnEnum.tomorrow_future) {
			@Override
			public Double getObject() {
				return optionsMonth.getContract() - apiObject.getIndex();
			}
		});
		addColumn(new MyColumnSql<Integer>(this,  MySqlColumnEnum.basketUp) {
			@Override
			public Integer getObject() {
				return apiObject.getBasketUp();
			}
		});
		addColumn(new MyColumnSql<Integer>(this, MySqlColumnEnum.basketDown) {
			@Override
			public Integer getObject() {
				return apiObject.getBasketDown();
			}
		});
		addColumn(new MyColumnSql<Integer>(this, MySqlColumnEnum.contract_bid_ask_counter) {
			@Override
			public Integer getObject() {
				return optionsMonth.getConBidAskCounter();
			}
		});
		addColumn(new MyColumnSql<Double>(this, MySqlColumnEnum.equalMove) {
			@Override
			public Double getObject() {
				return apiObject.getEqualLiveMove();
			}
		});
		addColumn(new MyColumnSql<String>(this,  MySqlColumnEnum.optionsWeek) {
			@Override
			public String getObject() {
				return optionsWeek.getAsJson().toString();
			}
		});
		addColumn(new MyColumnSql<String>(this,  MySqlColumnEnum.optionsMonth) {
			@Override
			public String getObject() {
				return optionsMonth.getAsJson().toString();
			}
		});		
	}
}
