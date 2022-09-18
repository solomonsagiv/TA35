package charts.myChart;

import api.ApiObject;
import dataBase.DataBaseHandler;
import dataBase.Factories;
import dataBase.mySql.MySql;
import dataBase.mySql.Queries;
import exp.Exp;
import java.sql.ResultSet;

public class TimeSeriesFactory {

    public static int step_second = 10;

    public static MyTimeSeries get_serie(String serie_name) {

        switch (serie_name.toUpperCase()) {

            case Factories.TimeSeries.FUTURE_WEEK:
                return new MyTimeSeries(Factories.TimeSeries.FUTURE_WEEK) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.FUT_WEEK, Queries.RAW);
                        DataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int serie_id = Factories.IDs.FUT_WEEK;
                        double val = Queries.handle_rs(Queries.get_last_record_mega(serie_id, MySql.RAW));
                        setValue(val);
                    }
                };

            case Factories.TimeSeries.FUTURE_MONTH:
                return new MyTimeSeries(Factories.TimeSeries.FUTURE_MONTH) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.FUT_MONTH, Queries.RAW);
                        DataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int serie_id = Factories.IDs.FUT_MONTH;
                        double val = Queries.handle_rs(Queries.get_last_record_mega(serie_id, MySql.RAW));
                        setValue(val);
                    }
                };



            case Factories.TimeSeries.INDEX_WITH_BID_ASK:
                return new MyTimeSeries(Factories.TimeSeries.INDEX_WITH_BID_ASK) {

                    @Override
                    public double getValue() {
                        double ind_with_bid_ask = (ApiObject.getInstance().getIndex_ask() + ApiObject.getInstance().getIndex_bid()) / 2;
                        return ind_with_bid_ask;
                    }

                    @Override
                    public void load() {
                        int bid_id = Factories.IDs.INDEX_BID;
                        int ask_id = Factories.IDs.INDEX_ASK;
                        ResultSet rs = Queries.get_index_with_bid_ask_series(bid_id, ask_id);
                        DataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {

                    }
                };

            case Factories.TimeSeries.DF_4_CDF_OLD:
                return new MyTimeSeries(Factories.TimeSeries.DF_4_CDF_OLD) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.DF_4_old, Queries.CDF);
                        DataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int serie_id = Factories.IDs.DF_4_old;
                        double val = Queries.handle_rs(Queries.get_last_record_mega(serie_id, MySql.CDF));
                        setValue(val);
                    }
                };

            case Factories.TimeSeries.DF_8_CDF_OLD:
                return new MyTimeSeries(Factories.TimeSeries.DF_8_CDF_OLD) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.DF_8_old, Queries.CDF);
                        DataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int serie_id = Factories.IDs.DF_8_old;
                        double val = Queries.handle_rs(Queries.get_last_record_mega(serie_id, MySql.CDF));
                        setValue(val);
                    }
                };

            case Factories.TimeSeries.DF_5_CDF_OLD:
                return new MyTimeSeries(Factories.TimeSeries.DF_5_CDF_OLD) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.DF_5_old, Queries.CDF);
                        DataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int serie_id = Factories.IDs.DF_5_old;
                        double val = Queries.handle_rs(Queries.get_last_record_mega(serie_id, MySql.CDF));
                        setValue(val);
                    }
                };


            case Factories.TimeSeries.DF_6_CDF_OLD:
                return new MyTimeSeries(Factories.TimeSeries.DF_6_CDF_OLD) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.DF_6_old, Queries.CDF);
                        DataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int serie_id = Factories.IDs.DF_6_old;
                        double val = Queries.handle_rs(Queries.get_last_record_mega(serie_id, MySql.CDF));
                        setValue(val);
                    }
                };

            case Factories.TimeSeries.DF_2_CDF:
                return new MyTimeSeries(Factories.TimeSeries.DF_2_CDF) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.DF_2, Queries.CDF);
                        DataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int serie_id = Factories.IDs.DF_2;
                        double val = Queries.handle_rs(Queries.get_last_record_mega(serie_id, MySql.CDF));
                        setValue(val);
                    }
                };

            case Factories.TimeSeries.DF_7_CDF:
                return new MyTimeSeries(Factories.TimeSeries.DF_7_CDF) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.DF_7, Queries.CDF);
                        DataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int serie_id = Factories.IDs.DF_7;
                        double val = Queries.handle_rs(Queries.get_last_record_mega(serie_id, MySql.CDF));
                        setValue(val);
                    }
                };

            case Factories.TimeSeries.DF_8_CDF:
                return new MyTimeSeries(Factories.TimeSeries.DF_8_CDF) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.DF_8_DE_CORR, Queries.CDF);
                        DataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        int serie_id = Factories.IDs.DF_8_DE_CORR;
                        double val = Queries.handle_rs(Queries.get_last_record_mega(serie_id, MySql.CDF));
                        setValue(val);
                    }
                };


            case Factories.TimeSeries.OP_AVG_WEEK_60:
                return new MyTimeSeries(Factories.TimeSeries.OP_AVG_WEEK_60) {
                    @Override
                    public double getValue() {
                        return ApiObject.getInstance().getExps().getWeek().getOp_avg_60();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.OP_AVG_60, MySql.RAW);
                        DataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        double val = Queries.handle_rs(Queries.get_last_record_mega(Factories.IDs.OP_AVG_60, MySql.RAW));
                        setValue(val);

                        Exp exp = ApiObject.getInstance().getExps().getWeek();
                        exp.setOp_avg_60(val);
                    }
                };

            case Factories.TimeSeries.OP_AVG_WEEK_5:
                return new MyTimeSeries(Factories.TimeSeries.OP_AVG_WEEK_5) {
                    @Override
                    public double getValue() {
                        return ApiObject.getInstance().getExps().getWeek().getOp_avg_5();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.OP_AVG_5, MySql.RAW);
                        DataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        double val = Queries.handle_rs(Queries.get_last_record_mega(Factories.IDs.OP_AVG_5, MySql.RAW));
                        setValue(val);

                        Exp exp = ApiObject.getInstance().getExps().getWeek();
                        exp.setOp_avg_5(val);
                    }
                };

            case Factories.TimeSeries.CONTINUE_OP_AVG_WEEK_240:
                return new MyTimeSeries(Factories.TimeSeries.CONTINUE_OP_AVG_WEEK_240) {

                    @Override
                    public double getValue() {
                        return ApiObject.getInstance().getExps().getWeek().getContinue_op_avg_240();
                    }

                    @Override
                    public void load() {
                        ResultSet rs = Queries.get_serie_mega_table(Factories.IDs.OP_AVG_240_CONTINUE, MySql.RAW);
                        DataBaseHandler.loadSerieData(rs, this);
                    }

                    @Override
                    public void updateData() {
                        double val = Queries.handle_rs(Queries.get_last_record_mega(Factories.IDs.OP_AVG_240_CONTINUE, MySql.RAW));
                        setValue(val);

                        Exp exp = ApiObject.getInstance().getExps().getWeek();
                        exp.setContinue_op_avg_240(val);
                    }
                };



            case Factories.TimeSeries.DF_2_SLOW:
                return new MyTimeSeries(Factories.TimeSeries.DF_2_SLOW) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                    }

                    @Override
                    public void updateData() {
                        int serie_id = Factories.IDs.DF_2;
                        double val = Queries.handle_rs(Queries.get_df_cdf_by_frame(serie_id, 21600));
                        setValue(val);
                    }
                };

            case Factories.TimeSeries.DF_7_SLOW:
                return new MyTimeSeries(Factories.TimeSeries.DF_7_SLOW) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                    }

                    @Override
                    public void updateData() {
                        int serie_id = Factories.IDs.DF_7;
                        double val = Queries.handle_rs(Queries.get_df_cdf_by_frame(serie_id, 21600));
                        setValue(val);
                    }
                };

            case Factories.TimeSeries.DF_8_SLOW:
                return new MyTimeSeries(Factories.TimeSeries.DF_8_SLOW) {

                    @Override
                    public double getValue() {
                        return super.getValue();
                    }

                    @Override
                    public void load() {
                    }

                    @Override
                    public void updateData() {
                        int serie_id = Factories.IDs.DF_8_DE_CORR;
                        double val = Queries.handle_rs(Queries.get_df_cdf_by_frame(serie_id, 21600));
                        setValue(val);
                    }
                };

            default:
                break;
        }
        return null;
    }
}
