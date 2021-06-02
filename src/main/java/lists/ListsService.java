package lists;

import exp.ExpMonth;
import exp.ExpWeek;
import service.MyBaseService;
import service.ServiceEnum;

import java.time.LocalDateTime;

// Regular list updater
public class ListsService extends MyBaseService {

    ExpWeek week;
    ExpMonth month;

    public ListsService() {
		super();
		week = apiObject.getExpWeek();
		month = apiObject.getExpMonth();
	}
    
    @Override
    public void go() {
        insert( );
    }
    
    @Override
    public String getName() {
        return "lists";
    }

    @Override
    public int getSleep() {
        return 1000;
    }

    @Override
    public ServiceEnum getType() {
        return ServiceEnum.REGULAR_LISTS;
    }

    private void insert() {
    	try {

            System.out.println("Lists " + hashCode());

            LocalDateTime now = LocalDateTime.now();

            week.getOptions().getOpChartList().add(new MyChartPoint(now, week.getOptions().getOp()));
            month.getOptions().getOpChartList().add(new MyChartPoint(now, month.getOptions().getOp()));

            week.getOptions().getDeltaChartList().add(new MyChartPoint(now, week.getOptions().getDelta()));
            month.getOptions().getDeltaChartList().add(new MyChartPoint(now, month.getOptions().getDelta()));

            week.getOptions().getConBidAskCounterList().add(new MyChartPoint(now, week.getOptions().getConBidAskCounter()));
            month.getOptions().getConBidAskCounterList().add(new MyChartPoint(now, month.getOptions().getConBidAskCounter()));

    	    apiObject.getIndexChartList().add(new MyChartPoint(now, apiObject.getIndex()));
            apiObject.getIndBasketsList().add(new MyChartPoint(now, apiObject.getBasketUp() - apiObject.getBasketDown()));

            apiObject.getStocksHandler().getIndDeltaList().add(new MyChartPoint(now, apiObject.getStocksHandler().getDelta()));
            apiObject.getStocksHandler().getIndDeltaNoBasketsList().add(new MyChartPoint(now, apiObject.getStocksHandler().getDeltaNoBaskets()));

    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
}