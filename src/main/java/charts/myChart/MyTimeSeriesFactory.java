package charts.myChart;

import dataBase.DataBaseHandler;
import dataBase.Factories;
import dataBase.mySql.Queries;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyTimeSeriesFactory {

    public static int step_second = 5;

    public static MyTimeSeries get_serie(String serie_name) {
        
        switch (serie_name.toUpperCase()) {
            // INDEX
            case Factories.TimeSeries.INDEX_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.INDEX_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getIndex();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie(Factories.Tables.SAGIV_INDEX_TABLE, step_second);
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
                    public double getData() {
                        return apiObject.getExps().getWeek().getOptions().getTotal_delta();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_cumulative_sum(Factories.Tables.SAGIV_DELTA_WEEK_TABLE, step_second);
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
                    public double getData() {
                        return apiObject.getExps().getMonth().getOptions().getTotal_delta();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_cumulative_sum(Factories.Tables.SAGIV_DELTA_MONTH_TABLE, step_second);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.FUTURE_WEEK_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.FUTURE_WEEK_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getExps().getWeek().getOptions().getContract();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie(Factories.Tables.SAGIV_FUT_WEEK_TABLE, step_second);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.INDEX_WITH_BID_ASK_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.INDEX_WITH_BID_ASK_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        double ind_with_bid_ask = (apiObject.getIndex_ask() + apiObject.getIndex_bid()) / 2;
                        return ind_with_bid_ask;
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_index_with_bid_ask_series(step_second);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.DF_V_4_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.DF_V_4_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        String table_location = Factories.Tables.DF_TABLE;
                        ResultSet rs = Queries.get_last_x_min_record_from_decision_func(table_location, 2, 4, minuts);

                        while (true) {
                            try {
                                if (!rs.next()) break;
                                System.out.println(rs.getString("time") + ", " + rs.getDouble("value"));
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                        return rs;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getV4();
                    }

                    @Override
                    public void load() {
//                        ResultSet rs = Queries.get_last_x_min_record_from_decision_func(Factories.Tables.DF_TABLE, 2, 4, Queries.START_OF_THE_DAY_MIN);
//                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };
            case Factories.TimeSeries.DF_V_8_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.DF_V_8_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        String table_location = Factories.Tables.DF_TABLE;
                        ResultSet rs = Queries.get_last_x_min_record_from_decision_func(table_location, 2, 8, minuts);
                        return rs;
                    }
                    
                    @Override
                    public double getData() {
                        return apiObject.getV8();
                    }

                    @Override
                    public void load() {
//                        ResultSet rs = Queries.get_last_x_min_record_from_decision_func(Factories.Tables.DF_TABLE, 2, 8, Queries.START_OF_THE_DAY_MIN);
//                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };
            case Factories.TimeSeries.DF_V_5_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.DF_V_5_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        String table_location = Factories.Tables.DF_TABLE;
                        ResultSet rs = Queries.get_last_x_min_record_from_decision_func(table_location, 2, 5, minuts);
                        while (true) {
                            try {
                                if (!rs.next()) break;
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                        return rs;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getV5();
                    }

                    @Override
                    public void load() {
//                        ResultSet rs = Queries.get_last_x_min_record_from_decision_func(Factories.Tables.DF_TABLE, 2, 4, Queries.START_OF_THE_DAY_MIN);
//                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.V_102_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.V_102_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        String table_location = Factories.Tables.DF_TABLE;
                        ResultSet rs = Queries.get_last_x_min_record_from_decision_func(table_location, 1, 102, minuts);
                        return rs;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getV103();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_last_x_min_record_from_decision_func(Factories.Tables.DF_TABLE, 1, 102, Queries.START_OF_THE_DAY_MIN);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };


            case Factories.TimeSeries.V_107_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.V_107_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        String table_location = Factories.Tables.DF_TABLE;
                        ResultSet rs = Queries.get_last_x_min_record_from_decision_func(table_location, 1, 107, minuts);
                        return rs;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getV107();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_last_x_min_record_from_decision_func(Factories.Tables.DF_TABLE, 1, 107, Queries.START_OF_THE_DAY_MIN);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.OP_AVG_WEEK_60_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.OP_AVG_WEEK_60_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getExps().getWeek().getOp_avg_60();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie(Factories.Tables.OP_AVG_60, step_second);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.OP_AVG_WEEK_5_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.OP_AVG_WEEK_5_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getExps().getWeek().getOp_avg_5();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie(Factories.Tables.OP_AVG_5, step_second);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.OP_AVG_WEEK_15_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.OP_AVG_WEEK_15_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getExps().getWeek().getOp_avg_15();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie(Factories.Tables.OP_AVG_15, step_second);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.CONTINUE_OP_AVG_WEEK_240_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.CONTINUE_OP_AVG_WEEK_240_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getExps().getWeek().getContinue_op_avg_240();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.op_avg_continue_serie(Factories.Tables.FUT_WEEK_TABLE, 14000, step_second);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.DELTA_MIX_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.DELTA_MIX_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getExps().get_delta();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_cumulative_sum(Factories.Tables.DELTA_MIX_TABLE, step_second);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };
            case Factories.TimeSeries.DELTA_WEEK_MONTH_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.DELTA_WEEK_MONTH_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getExps().get_delta();
                    }

                    @Override
                    public void load() {

                    }
                };
            case Factories.TimeSeries.OP_AVG_WEEK_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.DELTA_WEEK_AVG_60_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getExps().getWeek().getOp_avg();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.op_avg_cumulative(Factories.Tables.SAGIV_INDEX_TABLE, Factories.Tables.SAGIV_FUT_WEEK_TABLE);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };
            case Factories.TimeSeries.INDEX_DELTA_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.INDEX_DELTA_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getStocksHandler().getDelta();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_cumulative_sum(Factories.Tables.INDEX_DELTA_TABLE, step_second);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };
            case Factories.TimeSeries.OP_AVG_MONTH_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.OP_AVG_MONTH_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getExps().getMonth().getOp_avg();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.op_avg_cumulative(Factories.Tables.SAGIV_INDEX_TABLE, Factories.Tables.SAGIV_FUT_MONTH_TABLE);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.BID_ASK_COUNTER_WEEK_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.BID_ASK_COUNTER_WEEK_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getExps().getWeek().getOptions().getConBidAskCounter();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_cumulative_sum(Factories.Tables.BID_ASK_COUNTER_WEEK_TABLE, step_second);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.BID_ASK_COUNTER_MONTH_SERIE:
                return new MyTimeSeries(Factories.TimeSeries.BID_ASK_COUNTER_MONTH_SERIE) {
                    @Override
                    public ResultSet load_last_x_time(int minuts) {
                        return null;
                    }

                    @Override
                    public double getData() {
                        return apiObject.getExps().getMonth().getOptions().getConBidAskCounter();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_cumulative_sum(Factories.Tables.BID_ASK_COUNTER_MONTH_TABLE, step_second);
                        DataBaseHandler.loadSerieData(rs, this);
                    }
                };


            default:
                break;
        }
        return null;
    }
}
