package counter;

import api.ApiObject;
import arik.Arik;
import dataBase.DataBaseHandler;
import org.json.JSONArray;
import threads.MyThread;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class BackGroundRunner extends MyThread implements Runnable {

    double rando_start;
    double rando_end;
    double rando;
    boolean Rando = false;
    double bid;
    double ask;
    String status;
    LocalTime current_time;

    String preOpen = "preopen";
    String streamMarket = "stream";
    String randomally = "rando";
    String endMarket = "end";

    public static boolean preTradingBool = false;
    public static boolean streamMarketBool = false;
    public static boolean randomallyBool = false;
    public static boolean endMarketBool = false;
    boolean exported = false;
    boolean reset = false;

    Color lightGreen = new Color(12, 135, 0);
    Color lightRed = new Color(229, 19, 0);

    // Rando
    LocalTime start_rando = LocalTime.of(17, 20, 0);
    LocalTime end_rando = LocalTime.of(17, 27, 0);
    LocalTime end_day = LocalTime.of(17, 45, 0);

    JSONArray j;

    ApiObject apiObject = ApiObject.getInstance();

    Updater updater;

    public BackGroundRunner() {
        super();
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

            while (true) {
                try {
                    // Sleep
                    Thread.sleep(1000);

                    bid = apiObject.getIndex_bid();
                    ask = apiObject.getIndex_ask();
                    current_time = LocalTime.now();

                    System.out.println(" Running ");

                    // Wait for load
                    if (apiObject.isDbLoaded()) {

                        // Pre trading
                        if (apiObject.getStatus().contains(preOpen) && !preTradingBool) {
                            preTradingBool = true;
                        }

                        // Auto start
                        if (apiObject.getStatus().contains(streamMarket) && !streamMarketBool && current_time.isAfter(LocalTime.of(9, 57, 0)) && !apiObject.isStarted()) {
                            apiObject.setFutureOpen(apiObject.getExpMonth().getOptions().getContract());
                            apiObject.start();
                            streamMarketBool = true;
                            System.out.println(" Started ");
                        }

                        // Rando
                        if (apiObject.getStatus().contains(randomally) && !randomallyBool) {
                            streamMarketBool = false;
                            randomallyBool = true;
                            startRando();
                            apiObject.getServiceHandler().removeService(apiObject.getLogic());
                        }

                        // End of rando
                        if (apiObject.getStatus().contains(endMarket) && randomallyBool) {
                            randomallyBool = false;
                            endRando();
                        }

                        // End of Day
                        if (apiObject.getStatus().contains(endMarket) && current_time.isAfter(end_day) && !endMarketBool && !exported) {
                            apiObject.close();
                            endMarketBool = true;
                            exported = true;
                        }
                    } else {
                    	try {
                             System.out.println("Loading...");
						DataBaseHandler dataBaseHandler = new DataBaseHandler();
						dataBaseHandler.load_data();
					} catch (Exception e) {
                    		e.printStackTrace();
						Arik.getInstance().sendMessage(Arik.sagivID, "TA35 load data failed", null);
					}
                    	apiObject.setDbLoaded(true);
				}
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String str(Object o) {
        return String.valueOf(o);
    }

    // floor
    private static double floorDouble(double d) {
        return Math.floor(d * 1000) / 1000;
    }

    // Start rando
    public void startRando() throws Exception {
        rando_start = apiObject.getLast();
    }

    // End rando
    public void endRando() throws Exception {
        rando_end = apiObject.getLast();
        rando = floorDouble(((rando_end - rando_start) / rando_end) * 100);
        apiObject.setRando(rando);
        setColorPresent(WindowTA35.rando, floorDouble(rando));
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
