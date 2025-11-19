package charts.myChart;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import dataBase.Factories;
import dataBase.IDataBaseHandler;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import exp.Exp;
import options.Options;
import races.Race_Logic;
import java.util.List;
import java.util.Map;

public class TimeSeriesFactory {

    public static int step_second = 10;

    public static MyTimeSeries get_serie(String serie_name, BASE_CLIENT_OBJECT client) {

        switch (serie_name.toUpperCase()) {

            case Factories.TimeSeries.INDEX_AVG_3600:
                return new MyTimeSeries(Factories.TimeSeries.INDEX_AVG_3600, client) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void updateData() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.INDEX);
                        double val = Queries.handle_rs(Queries.get_serie_moving_avg(id, 60, MySql.JIBE_PROD_CONNECTION));
                        setValue(val);
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.INDEX);
                        List<Map<String, Object>> rs = Queries.get_cumulative_avg_serie(id, 60, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.INDEX_AVG_900:
                return new MyTimeSeries(Factories.TimeSeries.INDEX_AVG_900, client) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void updateData() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.INDEX);
                        double val = Queries.handle_rs(Queries.get_serie_moving_avg(id, 15, MySql.JIBE_PROD_CONNECTION));
                        setValue(val);

                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.INDEX);
                        List<Map<String, Object>> rs = Queries.get_cumulative_avg_serie(id, 15, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }
                };


            case Factories.TimeSeries.ROLL_900:
                return new MyTimeSeries(Factories.TimeSeries.ROLL_900, client) {
                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ROLL_900);
                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ROLL_900);
                        double val = Queries.handle_rs(Queries.get_last_record_mega(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION));
                        setValue(val);
                    }
                };

            case Factories.TimeSeries.ROLL_3600:
                return new MyTimeSeries(Factories.TimeSeries.ROLL_3600, client) {
                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ROLL_3600);
                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ROLL_3600);
                        double val = Queries.handle_rs(Queries.get_last_record_mega(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION));
                        setValue(val);
                    }
                };

            case Factories.TimeSeries.FUTURE_WEEK:
                return new MyTimeSeries(Factories.TimeSeries.FUTURE_WEEK, client) {

                    @Override
                    public double getValue() {
                        return TA35.getInstance().getExps().getWeek().getOptions().getContract();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.FUTURE_WEEK);
                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                    }
                };

            case Factories.TimeSeries.FUTURE_MONTH:
                return new MyTimeSeries(Factories.TimeSeries.FUTURE_MONTH, client) {

                    @Override
                    public double getValue() {
                        return TA35.getInstance().getExps().getMonth().getOptions().getContract();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.FUTURE_MONTH);
                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                    }
                };


            case Factories.TimeSeries.MID_DEV:
                return new MyTimeSeries(Factories.TimeSeries.MID_DEV, client) {

                    @Override
                    public double getValue() {
                        return (client.getAsk() + client.getBid()) / 2;
                    }

                    @Override
                    public void load() {
                        int bid_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.BID);
                        int ask_id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ASK);
                        List<Map<String, Object>> rs = Queries.get_index_with_bid_ask_series(bid_id, ask_id, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {

                    }
                };

            case Factories.TimeSeries.DF_4_CDF_OLD:
                return new MyTimeSeries(Factories.TimeSeries.DF_4_CDF_OLD, client) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.DF_4_CDF_OLD);
                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.DF_4_CDF_OLD);
                        double val = Queries.handle_rs(Queries.get_last_record_mega(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION));
                        setValue(val);
                    }
                };

            case Factories.TimeSeries.DF_8_CDF_OLD:
                return new MyTimeSeries(Factories.TimeSeries.DF_8_CDF_OLD, client) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.DF_8_CDF_OLD);
                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.DF_8_CDF_OLD);
                        double val = Queries.handle_rs(Queries.get_last_record_mega(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION));
                        setValue(val);
                    }
                };

            case Factories.TimeSeries.DF_5_CDF_OLD:
                return new MyTimeSeries(Factories.TimeSeries.DF_5_CDF_OLD, client) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.DF_5_CDF_OLD);
                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.DF_5_CDF_OLD);
                        double val = Queries.handle_rs(Queries.get_last_record_mega(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION));
                        setValue(val);
                    }
                };


            case Factories.TimeSeries.DF_6_CDF_OLD:
                return new MyTimeSeries(Factories.TimeSeries.DF_6_CDF_OLD, client) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.DF_6_CDF_OLD);
                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.DF_6_CDF_OLD);
                        double val = Queries.handle_rs(Queries.get_last_record_mega(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION));
                        setValue(val);
                    }
                };


            case Factories.TimeSeries.OP_AVG_WEEK_60:
                return new MyTimeSeries(Factories.TimeSeries.OP_AVG_WEEK_60, client) {
                    @Override
                    public double getValue() {
                        return TA35.getInstance().getExps().getWeek().getOp_avg_60();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_AVG_WEEK_60);

                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_AVG_WEEK_60);

                        double val = Queries.handle_rs(Queries.get_last_record_mega(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION));
                        setValue(val);

                        Exp exp = TA35.getInstance().getExps().getWeek();
                        exp.setOp_avg_60(val);
                    }
                };

            case Factories.TimeSeries.OP_AVG_WEEK_15:
                return new MyTimeSeries(Factories.TimeSeries.OP_AVG_WEEK_15, client) {
                    @Override
                    public double getValue() {
                        return TA35.getInstance().getExps().getWeek().getOp_avg_15();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_AVG_WEEK_15);

                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_AVG_WEEK_15);

                        double val = Queries.handle_rs(Queries.get_last_record_mega(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION));
                        setValue(val);

                        Exp exp = TA35.getInstance().getExps().getWeek();
                        exp.setOp_avg_15(val);
                    }
                };

            case Factories.TimeSeries.OP_AVG_240_CONTINUE:
                return new MyTimeSeries(Factories.TimeSeries.OP_AVG_240_CONTINUE, client) {

                    @Override
                    public double getValue() {
                        return TA35.getInstance().getExps().getWeek().getContinue_op_avg_240();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_AVG_240_CONTINUE);

                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_AVG_240_CONTINUE);

                        double val = Queries.handle_rs(Queries.get_last_record_mega(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION));
                        setValue(val);

                        Exp exp = TA35.getInstance().getExps().getWeek();
                        exp.setContinue_op_avg_240(val);
                    }
                };


            case Factories.TimeSeries.INDEX_RACES_WI:
                return new MyTimeSeries(Factories.TimeSeries.INDEX_RACES_WI, client) {

                    @Override
                    public double getValue() {
                        return TA35.getInstance().getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX).get_r_one_points();
                    }


                    @Override
                    public void updateData() {
//                        int serie_id = client.getMySqlService().getDataBaseHandler().getSerie_ids().get(TimeSeriesHandler.INDEX_RACES_PROD);
//                        setValue(MySql.Queries.handle_rs(Objects.requireNonNull(MySql.Queries.get_last_record_mega(serie_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION))));
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.INDEX_RACES_WI);

                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.WEEK_RACES_WI:
                return new MyTimeSeries(Factories.TimeSeries.WEEK_RACES_WI, client) {

                    @Override
                    public double getValue() {
                        return TA35.getInstance().getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_INDEX).get_r_two_points();
                    }

                    @Override
                    public void updateData() {
//                        int serie_id = client.getMySqlService().getDataBaseHandler().getSerie_ids().get(TimeSeriesHandler.INDEX_RACES_PROD);
//                        setValue(MySql.Queries.handle_rs(Objects.requireNonNull(MySql.Queries.get_last_record_mega(serie_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION))));
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.WEEK_RACES_WI);

                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.WEEK_RACES_WM:
                return new MyTimeSeries(Factories.TimeSeries.WEEK_RACES_WM, client) {

                    @Override
                    public double getValue() {
                        return TA35.getInstance().getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH).get_r_two_points();
                    }


                    @Override
                    public void updateData() {
//                        int serie_id = client.getMySqlService().getDataBaseHandler().getSerie_ids().get(TimeSeriesHandler.INDEX_RACES_PROD);
//                        setValue(MySql.Queries.handle_rs(Objects.requireNonNull(MySql.Queries.get_last_record_mega(serie_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION))));
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.WEEK_RACES_WM);

                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }
                };


            case Factories.TimeSeries.MONTH_RACES_WM:
                return new MyTimeSeries(Factories.TimeSeries.MONTH_RACES_WM, client) {

                    @Override
                    public double getValue() {
                        return TA35.getInstance().getRacesService().get_race_logic(Race_Logic.RACE_RUNNER_ENUM.WEEK_MONTH).get_r_one_points();
                    }


                    @Override
                    public void updateData() {
//                        int serie_id = client.getMySqlService().getDataBaseHandler().getSerie_ids().get(TimeSeriesHandler.INDEX_RACES_PROD);
//                        setValue(MySql.Queries.handle_rs(Objects.requireNonNull(MySql.Queries.get_last_record_mega(serie_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION))));
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MONTH_RACES_WM);

                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }
                };


            case Factories.TimeSeries.MONTH_BID_ASK_COUNTER_PROD:
                return new MyTimeSeries(Factories.TimeSeries.MONTH_BID_ASK_COUNTER_PROD, client) {

                    @Override
                    public double getValue() {
                        Options options = client.getExps().getMonth().getOptions();
                        return options.getBidAskCounter();
                    }

                    @Override
                    public void updateData() {
//                        int serie_id = client.getMySqlService().getDataBaseHandler().getSerie_ids().get(TimeSeriesHandler.INDEX_RACES_PROD);
//                        setValue(MySql.Queries.handle_rs(Objects.requireNonNull(MySql.Queries.get_last_record_mega(serie_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION))));
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.MONTH_BID_ASK_COUNTER_PROD);

                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }
                };

            case Factories.TimeSeries.WEEK_BID_ASK_COUNTER_PROD:
                return new MyTimeSeries(Factories.TimeSeries.WEEK_BID_ASK_COUNTER_PROD, client) {

                    @Override
                    public double getValue() {
                        Options options = client.getExps().getWeek().getOptions();
                        return options.getBidAskCounter();
                    }

                    @Override
                    public void updateData() {
//                        int serie_id = client.getMySqlService().getDataBaseHandler().getSerie_ids().get(TimeSeriesHandler.INDEX_RACES_PROD);
//                        setValue(MySql.Queries.handle_rs(Objects.requireNonNull(MySql.Queries.get_last_record_mega(serie_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION))));
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.WEEK_BID_ASK_COUNTER_PROD);

                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.CDF, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }
                };


            case Factories.TimeSeries.TRADING_STATUS:
                return new MyTimeSeries(Factories.TimeSeries.TRADING_STATUS, client) {

                    @Override
                    public double getValue() {
                        return client.getTrading_status();
                    }

                    @Override
                    public void updateData() {
                    }

                    @Override
                    public void load() {
                    }
                };


            case Factories.TimeSeries.OP_MONTH_INTEREST_PROD:
                return new MyTimeSeries(Factories.TimeSeries.FUTURE_WEEK, client) {

                    @Override
                    public double getValue() {
                        return client.getOp_month_interest();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_MONTH_INTEREST_PROD);
                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                    }
                };

            case Factories.TimeSeries.OP_MONTH_INTEREST_AVG_PROD:
                return new MyTimeSeries(Factories.TimeSeries.OP_MONTH_INTEREST_AVG_PROD, client) {

                    @Override
                    public double getValue() {
                        return client.getOp_month_interest_avg();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_MONTH_INTEREST_AVG_PROD);
                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.OP_MONTH_INTEREST_AVG_PROD);
                        double val = Queries.handle_rs(Queries.get_last_record_mega(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION));
                        setValue(val);
                        client.setOp_month_interest_avg(val);
                    }
                };

            case Factories.TimeSeries.ROLL_INTEREST_PROD:
                return new MyTimeSeries(Factories.TimeSeries.FUTURE_WEEK, client) {

                    @Override
                    public double getValue() {
                        return client.getRoll_interest();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ROLL_INTEREST_PROD);
                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                    }
                };

            case Factories.TimeSeries.ROLL_INTEREST_AVG_PROD:
                return new MyTimeSeries(Factories.TimeSeries.OP_MONTH_INTEREST_AVG_PROD, client) {

                    @Override
                    public double getValue() {
                        return client.getRoll_interest_avg();
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ROLL_INTEREST_AVG_PROD);
                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.ROLL_INTEREST_AVG_PROD);
                        double val = Queries.handle_rs(Queries.get_last_record_mega(id, MySql.RAW, MySql.JIBE_PROD_CONNECTION));
                        setValue(val);
                        client.setRoll_interest_avg(val);

                    }
                };

            case Factories.TimeSeries.STOCKS_TOT_BA_WEIGHT_PROD:
                return new MyTimeSeries(Factories.TimeSeries.STOCKS_TOT_BA_WEIGHT_PROD, client) {

                    @Override
                    public double getValue() {
                        return TA35.getInstance().getBa_total_positive_weight();
                    }

                    @Override
                    public void updateData() {
//                        int serie_id = client.getMySqlService().getDataBaseHandler().getSerie_ids().get(TimeSeriesHandler.INDEX_RACES_PROD);
//                        setValue(MySql.Queries.handle_rs(Objects.requireNonNull(MySql.Queries.get_last_record_mega(serie_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION))));
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.STOCKS_TOT_BA_WEIGHT_PROD);

                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW_NO_MODULU, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }
                };


            case Factories.TimeSeries.STOCKS_TOT_DELTA_WEIGHT_PROD:
                return new MyTimeSeries(Factories.TimeSeries.STOCKS_TOT_DELTA_WEIGHT_PROD, client) {

                    @Override
                    public double getValue() {
                        return TA35.getInstance().getDelta_potisive_weight();
                    }

                    @Override
                    public void updateData() {
//                        int serie_id = client.getMySqlService().getDataBaseHandler().getSerie_ids().get(TimeSeriesHandler.INDEX_RACES_PROD);
//                        setValue(MySql.Queries.handle_rs(Objects.requireNonNull(MySql.Queries.get_last_record_mega(serie_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION))));
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.STOCKS_TOT_DELTA_WEIGHT_PROD);

                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW_NO_MODULU, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }
                };


                case Factories.TimeSeries.COUNTER_2_TOT_WEIGHT_PROD:
                return new MyTimeSeries(Factories.TimeSeries.COUNTER_2_TOT_WEIGHT_PROD, client) {

                    @Override
                    public double getValue() {
                        return TA35.getInstance().getCounter_2_tot_weight();
                    }

                    @Override
                    public void updateData() {
//                        int serie_id = client.getMySqlService().getDataBaseHandler().getSerie_ids().get(TimeSeriesHandler.INDEX_RACES_PROD);
//                        setValue(MySql.Queries.handle_rs(Objects.requireNonNull(MySql.Queries.get_last_record_mega(serie_id, MySql.CDF, MySql.JIBE_PROD_CONNECTION))));
                    }

                    @Override
                    public void load() {
                        int id = client.getTimeSeriesHandler().get_id(Factories.TimeSeries.COUNTER_2_TOT_WEIGHT_PROD);

                        List<Map<String, Object>> rs = Queries.get_serie_mega_table(id, MySql.RAW_NO_MODULU, MySql.JIBE_PROD_CONNECTION);
                        IDataBaseHandler.loadSerieData(rs, this);
                    }
                };
                

            default:
                break;
        }
        return null;
    }
}
