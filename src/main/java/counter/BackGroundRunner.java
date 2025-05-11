package counter;

import api.Manifest;
import api.TA35;
import arik.Arik;
import dataBase.DataBaseHandler;
import org.json.JSONArray;
import races.Stocks_Race_Service;
import service.DataReaderService;
import threads.MyThread;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class BackGroundRunner extends MyThread implements Runnable {

    double bid;
    double ask;
    LocalTime current_time;

    String preOpen = "preopen";
    String streamMarket = "stream";
    String randomally = "rando";
    String endMarket = "end";

    public static String excelPath = "C://Users/yosef/Desktop/[TA35.xlsm]DDE";

    public static boolean preTradingBool = false;
    public static boolean streamMarketBool = false;
    public static boolean randomallyBool = false;
    public static boolean endMarketBool = false;
    boolean exported = false;

    Color lightGreen = new Color(12, 135, 0);
    Color lightRed = new Color(229, 19, 0);

    // Rando
    LocalTime start_rando = LocalTime.of(17, 20, 0);
    LocalTime end_rando = LocalTime.of(17, 27, 0);
    LocalTime end_day = LocalTime.of(17, 45, 0);

    JSONArray j;

    api.TA35 client;


    public BackGroundRunner(TA35 client) {
        super();
        this.client = client;
        setRunnable(this);
    }

    @Override
    public void run() {
        runner();
    }

    int i = 0;

    // Runner
    @SuppressWarnings("static-access")
    public void runner() {
        try {
            if (LocalDate.now().getDayOfWeek().equals("SUNDAY")) {
                end_day = LocalTime.of(16, 0, 0);
                start_rando = LocalTime.of(15, 40, 0);
                end_rando = LocalTime.of(15, 50, 0);
            }

            // Data reader
            new DataReaderService(client, BackGroundRunner.excelPath);
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

                        // Pre trading
                        if (client.getStatus().contains(preOpen) && !preTradingBool) {
                            preTradingBool = true;
                        }

                        // Auto start
                        if (client.getStatus().contains(streamMarket) && !streamMarketBool && current_time.isAfter(LocalTime.of(9, 59, 0)) && !client.isStarted() && ask > bid && ask - bid < 10) {
                            client.start();

                            if (Manifest.OPEN_CHART) {
                                WindowTA35.openCharts();
                            }

                            // Start stocks
                            start_stocks();

                            streamMarketBool = true;
                            System.out.println(" Started ");
                        }

                        // Rando
                        if (client.getStatus().contains(randomally) && !randomallyBool) {
                            streamMarketBool = false;
                            randomallyBool = true;
                        }

                        // End of rando
                        if (client.getStatus().contains(endMarket) && randomallyBool) {
                            randomallyBool = false;
                        }

                        // End of Day
                        if (client.getStatus().contains(endMarket) && current_time.isAfter(end_day) && !endMarketBool && !exported) {
                            client.close();
                            endMarketBool = true;
                            exported = true;
                        }
                    } else {
                        try {
                            System.out.println("Loading...");
                            DataBaseHandler dataBaseHandler = new DataBaseHandler(client);
                            dataBaseHandler.load_data();
                            System.out.println("Loaded!!!");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Arik.getInstance().sendMessage(Arik.sagivID, "TA35 load data failed", null);
                        }
                        client.setDb_loaded(true);
                    }
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
            textField.setBackground(lightGreen);
            textField.setText(String.valueOf(text) + "% ");
        } else {
            textField.setBackground(lightRed);
            textField.setText(String.valueOf(text) + "% ");
        }
    }

    @Override
    public void initRunnable() {
        setRunnable(this);
    }
}
