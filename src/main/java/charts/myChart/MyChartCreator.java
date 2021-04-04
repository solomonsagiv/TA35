package charts.myChart;

import api.ApiObject;

public abstract class MyChartCreator implements IChartCreator {

    public final int INFINITE = 1000000000;

    protected ApiObject apiObject;

    protected MyProps props;
    
    public MyChartCreator( ApiObject client ) {
        this.apiObject = client;
    }
    
}
