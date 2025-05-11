package charts.myChart;

import api.TA35;

public abstract class MyChartCreator implements IChartCreator {

    public final int INFINITE = 1000000000;

    protected TA35 client;

    protected MyProps props;
    
    public MyChartCreator( TA35 client ) {
        this.client = client;
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
