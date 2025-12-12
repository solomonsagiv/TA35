package counter;

import api.Manifest;
import api.TA35;
import arik.Arik;
import locals.Themes;

import org.json.JSONArray;
import races.Stocks_Race_Service;
import service.NewDataReaderService;
import service.StocksReaderService;
import threads.MyThread;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class BackGroundRunner extends MyThread implements Runnable {

    double bid;
    double ask;
    LocalTime current_time;

    public static String preOpen = "preopen";
    public static String streamMarket = "stream";
    public static String randomally = "rando";
    public static String endMarket = "end";

    // public static String excelPath = "C://Users/yosef/Desktop/[TA35.xlsm]DDE";
    public static String excelPath = "C:/Users/yosef/OneDrive/Desktop/[ta calc.xlsx]DDE";

    public static boolean open_charts = false;
    boolean exported = false;

    // Use Themes colors instead of hardcoded colors

    // Rando
    LocalTime end_day = LocalTime.of(17, 25, 0);
    LocalTime start_day = LocalTime.of(9, 59, 0);
    JSONArray j;

    api.TA35 client;


    double index = 0;

    public BackGroundRunner(TA35 client) {
        super();
        this.client = client;
        setRunnable(this);
    }

    @Override
    public void run() {
        runner();
    }


    // Runner
    @SuppressWarnings("static-access")
    public void runner() {
        try {
            if (LocalDate.now().getDayOfWeek().equals("SUNDAY")) {
                end_day = LocalTime.of(16, 0, 0);
            }

            // Data reader
            new NewDataReaderService(client, BackGroundRunner.excelPath);
            new StocksReaderService(client, BackGroundRunner.excelPath);
            client.getServiceHandler().getHandler().start();

            while (true) {
                try {
                    // Sleep
                    Thread.sleep(1000);

                    bid = client.getBid();
                    ask = client.getAsk();
                    current_time = LocalTime.now();

                    // Wait for load
                    if (client.isDb_loaded()) {

                        // Open charts
                        if (StocksReaderService.initStocksCells && LocalTime.now().isAfter(start_day) && LocalTime.now().isBefore(end_day) 
                            && !open_charts && index != client.getIndex() && ask > bid && !client.isStarted()) {
                            client.setStatus(0);
                            client.start();
                            start_stocks();
                       
                            if (Manifest.OPEN_CHART) {
                                WindowTA35.openCharts();
                                open_charts = true;
                            }
                        }

                        // End of day
                        if (LocalTime.now().isAfter(end_day)) {
                            client.setStatus(1);
                            client.close();
                            exported = true;
                        }

                    } else {
                        try {
                            System.out.println("Loading...");
                            client.getDataBaseService().getDataBaseHandler().loadData();
                            System.out.println("Loaded!!!");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Arik.getInstance().sendMessage(Arik.sagivID, "TA35 load data failed", null);
                        }
                        client.setDb_loaded(true);
                    }

                    // Update index
                    index = client.getIndex();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start_stocks() {


        TA35.getInstance().setStocks_race_service(new Stocks_Race_Service(TA35.getInstance()));
    }

    private String str(Object o) {
        return String.valueOf(o);
    }

    // floor
    private static double floorDouble(double d) {
        return Math.floor(d * 1000) / 1000;
    }

    public double dbl(String string) {
        return Double.parseDouble(string);
    }

    // color setting function();
    public void setColorPresent(JTextField textField, double text) {
        if (text >= 0.0) {
            textField.setBackground(Themes.getGreenStatusColor());
            textField.setText(String.valueOf(text) + "% ");
        } else {
            textField.setBackground(Themes.getRedStatusColor());
            textField.setText(String.valueOf(text) + "% ");
        }
    }

    @Override
    public void initRunnable() {
        setRunnable(this);
    }
}
