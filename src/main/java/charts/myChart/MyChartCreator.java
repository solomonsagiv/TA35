package charts.myChart;

import api.ApiObject;

public abstract class MyChartCreator implements IChartCreator {

    public final int INFINITE = 1000000000;

    protected ApiObject apiObject;

    protected MyProps props;
    
    public MyChartCreator( ApiObject client ) {
        this.apiObject = client;
    }

    public void createChart() {
        new Thread(() ->{
            try {
                init();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
}
