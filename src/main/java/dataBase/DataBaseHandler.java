package dataBase;

import charts.myChart.MyTimeSeries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DataBaseHandler {

    public static void loadSerieData(ResultSet rs, MyTimeSeries timeSeries) {
        while (true) {
            try {
                if (!rs.next()) break;
                Timestamp timestamp = rs.getTimestamp(1);
                double value = rs.getDouble("value");
                timeSeries.add(timestamp.toLocalDateTime(), value);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
