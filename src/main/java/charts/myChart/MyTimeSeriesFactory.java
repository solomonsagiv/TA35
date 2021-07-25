package charts.myChart;

import dataBase.DataBaseHandler;
import dataBase.TablesFactory;
import dataBase.mySql.Queries;
import java.net.UnknownHostException;
import java.sql.ResultSet;

public class MyTimeSeriesFactory {

    public static final String INDEX_SERIE = "INDEX_SERIE";
    public static final String DELTA_WEEK_SERIE = "DELTA_WEEK_SERIE";
    public static final String DELTA_MONTH_SERIE = "DELTA_MONTH_SERIE";

    public static MyTimeSeries get_serie(String serie_name) {

        System.out.println(serie_name);


        switch (serie_name.toUpperCase()) {
            // INDEX
            case INDEX_SERIE:
                return new MyTimeSeries(INDEX_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getIndex();
                    }

                    @Override
                    public void load_data() {
                        ResultSet rs = Queries.get_index_serie();
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };
            case DELTA_WEEK_SERIE:
                return new MyTimeSeries(DELTA_WEEK_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getExpWeek().getOptions().getDelta();
                    }

                    @Override
                    public void load_data() {
                        ResultSet rs = Queries.get_serie_cumulative(TablesFactory.DELTA_WEEK_TABLE);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };
            case DELTA_MONTH_SERIE:
                return new MyTimeSeries(DELTA_MONTH_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getExpMonth().getOptions().getDelta();
                    }

                    @Override
                    public void load_data() {
                        ResultSet rs = Queries.get_serie_cumulative(TablesFactory.DELTA_MONTH_TABLE);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };
            default:
                break;

        }

        return null;

    }

}
