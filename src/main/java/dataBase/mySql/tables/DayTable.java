package dataBase.mySql.tables;

import java.time.LocalDate;
import java.time.LocalTime;

import dataBase.mySql.myBaseTables.MyDayTable;
import dataBase.mySql.mySqlComps.MyColumnSql;
import dataBase.mySql.mySqlComps.MySqlColumnEnum;
import options.Options;

public class DayTable extends MyDayTable {
	
    public DayTable(String name) {
		super(name);
	}
    
    @Override
    public void initColumns() {
    	
		Options optionsMonth = apiObject.getExpMonth().getOptions();
		Options optionsWeek = apiObject.getExpWeek().getOptions();
    	
        addColumn(new MyColumnSql<String>(this,  MySqlColumnEnum.date) {
            @Override
            public String getObject() {
                return LocalDate.now().toString();
            }
        });
        addColumn(new MyColumnSql<String>(this,  MySqlColumnEnum.time) {
            @Override
            public String getObject() {
                return LocalTime.now().toString();
            }
        });
        addColumn(new MyColumnSql<Double>(this,  MySqlColumnEnum.con) {
            @Override
            public Double getObject() {
                return optionsMonth.getContract();
            }
        });
        addColumn(new MyColumnSql<Double>(this,  MySqlColumnEnum.ind) {
            @Override
            public Double getObject() {
                return apiObject.getIndex();
            }
        });
        
        addColumn(new MyColumnSql<Double>(this, MySqlColumnEnum.indBid) {
            @Override
            public Double getObject() {
                return apiObject.getIndex_bid();
            }
        });
        addColumn(new MyColumnSql<Double>(this,  MySqlColumnEnum.indAsk) {
            @Override
            public Double getObject() {
                return apiObject.getIndex_ask();
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
                return apiObject.getOptimiTimer();
            }
        });
        addColumn(new MyColumnSql<Integer>(this,  MySqlColumnEnum.basketUp) {
            @Override
            public Integer getObject() {
                return apiObject.getBasketUp();
            }
        });
        addColumn(new MyColumnSql<Integer>(this,  MySqlColumnEnum.basketDown) {
            @Override
            public Integer getObject() {
                return apiObject.getBasketDown();
            }
        });
        addColumn(new MyColumnSql<Double>(this,  MySqlColumnEnum.OptimiMove) {
            @Override
            public Double getObject() {
                return apiObject.getOptimiLiveMove();
            }
        });
        addColumn(new MyColumnSql<Double>(this,  MySqlColumnEnum.pesimiMove) {
            @Override
            public Double getObject() {
                return apiObject.getPesimiLiveMove();
            }
        });
        
        addColumn(new MyColumnSql<Integer>(this, MySqlColumnEnum.indBidAskCounter) {
            @Override
            public Integer getObject() {
                return optionsMonth.getConBidAskCounter();
            }
        });
        
        addColumn(new MyColumnSql<Double>(this,  MySqlColumnEnum.base) {
            @Override
            public Double getObject() {
                return apiObject.getBase();
            }
        });
        
        addColumn(new MyColumnSql<String>(this, MySqlColumnEnum.optionsWeek) {
            @Override
            public String getObject() {
                return optionsWeek.getAsJson().toString();
            }
        });
        
        addColumn(new MyColumnSql<String>(this, MySqlColumnEnum.optionsMonth) {
            @Override
            public String getObject() {
                return optionsMonth.getAsJson().toString();
            }
        });
        
        
    }
}
