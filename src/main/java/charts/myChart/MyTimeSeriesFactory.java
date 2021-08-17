package charts.myChart;

import dataBase.DataBaseHandler;
import dataBase.Factories;
import dataBase.mySql.Queries;
import java.net.UnknownHostException;
import java.sql.ResultSet;

public class MyTimeSeriesFactory {

    public static MyTimeSeries get_serie(String serie_name) {

        System.out.println(serie_name);

        switch (serie_name.toUpperCase()) {
            // INDEX
            case Factories.TimeSeries.INDEX_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.INDEX_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getIndex();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_index_serie();
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };
            case Factories.TimeSeries.DELTA_WEEK_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.DELTA_WEEK_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getExpWeek().getOptions().getDelta();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_cumulative(Factories.Tables.DELTA_WEEK_TABLE);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };
            case Factories.TimeSeries.DELTA_MONTH_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.DELTA_MONTH_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getExpMonth().getOptions().getDelta();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_cumulative(Factories.Tables.DELTA_MONTH_TABLE);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };
            case Factories.TimeSeries.DELTA_WEEK_AVG_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.DELTA_WEEK_AVG_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getExpWeek().getDelta_avg();
                    }

                    @Override
                    public void load() {
//                        ResultSet rs = Queries.get_serie_cumulative(Factories.Tables.);
//                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.DELTA_WEEK_AVG_60_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.DELTA_WEEK_AVG_60_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getExpWeek().getDelta_avg_60();
                    }

                    @Override
                    public void load() {
//                        ResultSet rs = Queries.get_serie_cumulative(Factories.Tables.);
//                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.OP_AVG_WEEK_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.DELTA_WEEK_AVG_60_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getExpWeek().getOp_avg();
                    }

                    @Override
                    public void load() {
//                        ResultSet rs = Queries.get_serie_cumulative(Factories.Tables.);
//                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.OP_AVG_WEEK_60_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.OP_AVG_WEEK_60_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getExpWeek().getOp_avg_60();
                    }

                    @Override
                    public void load() {
//                        ResultSet rs = Queries.get_serie_cumulative(Factories.Tables.);
//                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.DELTA_MONTH_AVG_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.DELTA_MONTH_AVG_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getExpWeek().getDelta_avg();
                    }

                    @Override
                    public void load() {
//                        ResultSet rs = Queries.get_serie_cumulative(Factories.Tables.);
//                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.DELTA_MONTH_AVG_60_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.DELTA_MONTH_AVG_60_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getExpWeek().getDelta_avg_60();
                    }

                    @Override
                    public void load() {
//                        ResultSet rs = Queries.get_serie_cumulative(Factories.Tables.);
//                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.OP_AVG_MONTH_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.OP_AVG_MONTH_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getExpWeek().getOp_avg();
                    }

                    @Override
                    public void load() {
//                        ResultSet rs = Queries.get_serie_cumulative(Factories.Tables.);
//                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };


            case Factories.TimeSeries.OP_AVG_MONTH_60_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.OP_AVG_MONTH_60_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() throws UnknownHostException {
                        return apiObject.getExpWeek().getOp_avg_60();
                    }

                    @Override
                    public void load() {
//                        ResultSet rs = Queries.get_serie_cumulative(Factories.Tables.);
//                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };
            default:
                break;
        }
        return null;
    }

}
