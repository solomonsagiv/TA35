package arik;

import arik.dataHandler.DataHandler;
import arik.locals.Emojis;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.SendMessage;
import dataBase.mySql.MySql;
import locals.L;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import com.pengrad.telegrambot.TelegramBotAdapter;

public class Arik {

    public static boolean allow_trading = false;

    public static final boolean EVERYONE = false;

    public static int sagivID = 365117561;
    private static Arik arik;
    private ArikRunner arikRunner;
    private boolean running = false;
    private TelegramBot bot;
    private DataHandler dataHandler;

    private long updateId = 0;

    public static ArrayList<Long> accounts = new ArrayList<>();
    public static ArrayList<Long> slo = new ArrayList<>();

    private Arik() {

        bot = new TelegramBot("400524449:AAE1nFANVNd6Vlj44DKhQLD0fdtlE7MZFj0");
    }

    public static void main(String[] args) {
        Arik.getInstance().start();

    }

    // Get instance
    public static Arik getInstance() {
        if (arik == null) {
            arik = new Arik();
        }
        return arik;
    }

    public void start() {
        if (!running) {

            try {
                // Load from database
                load_from_db();

                // Data objects
                init_data_handler();

                // Arik messages runner
                arik_runner();

                System.out.println("Running");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    private void init_data_handler() {
        dataHandler = new DataHandler();
    }

    private void arik_runner() {
        arikRunner = new ArikRunner(this);
        arikRunner.start();
    }

    public static void load_from_db() throws Exception {
        List<Map<String, Object>> rs = MySql.select("select * from sagiv.arik_accounts;", MySql.JIBE_PROD_CONNECTION);
        for(Map<String, Object> row: rs) {
            try {
                long id = L.row_to_int(row.get("id"));
                boolean notification = (boolean) row.get("notification");

                if (id > 100) {
                    accounts.add(id);

//                 Slo accounts
                    if (notification) {
                        slo.add(id);
                    }
                }
            } catch (ClassCastException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void close() {
        arik = null;
        bot = null;
        arikRunner.close();
        running = false;
    }

    public void sendMessage(String text) {
        getBot().execute(new SendMessage(sagivID, text));
        updateId += 1;
    }

    public void sendErrorMessage(Exception e) {
        String text = e.getMessage() + "\n" + e.getCause();
        getBot().execute(new SendMessage(sagivID, text));
        updateId += 1;
    }

    public void sendMessage(String action, boolean success) {
        String text = action + " " + " success " + Emojis.check_mark;
        // Success
        if (success) {
            text += " " + " success " + Emojis.check_mark;
            sendMessage(text);
        } else {
            text += " " + " failed " + Emojis.stop;
            sendMessage(text);
        }
    }

    // Send message
    public void sendMessage(Update update, String text, Keyboard keyBoard) {
        if (keyBoard != null) {
            getBot().execute(new SendMessage(update.message().from().id(), text).replyMarkup(keyBoard));
            updateId = update.updateId() + 1;
        } else {
            getBot().execute(new SendMessage(update.message().from().id(), text));
            updateId = update.updateId() + 1;
        }
    }

    // Send message
    public void sendMessageToSlo(String text) {
        try {
            for (long person : slo) {
                sendMessage(person, text, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Send message
    public void sendMessageToEveryOne(String text) {
        try {
            if (EVERYONE) {
                for (long account : accounts) {
                    sendMessage(account, text, null);
                }
            } else {
                for (long account : slo) {
                    sendMessage(account, text, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Send message
    public void sendMessage(long id, String text, Keyboard keyBoard) {
        if (keyBoard != null) {
            getBot().execute(new SendMessage(id, text).replyMarkup(keyBoard));
            updateId += 1;
        } else {
            getBot().execute(new SendMessage(id, text));
            updateId += 1;
        }
    }

    // ----------- Getters and Setters ---------- //
    public long getUpdateId() {
        return updateId;
    }

    public void setUpdateId(int updateId) {
        this.updateId = updateId;
    }

    public ArikRunner getArikRunner() {
        return arikRunner;
    }

    public void setArikRunner(ArikRunner arikRunner) {
        this.arikRunner = arikRunner;
    }

    public TelegramBot getBot() {
        return bot;
    }

    public void setBot(TelegramBot bot) {
        this.bot = bot;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public void setDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }
}
