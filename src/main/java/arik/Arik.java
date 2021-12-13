package arik;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.SendMessage;
import dataBase.mySql.MySql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Arik {

    private static Arik arik;

    public static int sagivID = 365117561;

    private TelegramBot bot;

    private int updateId = 0;
    ArrayList<Integer> accounts = new ArrayList<>();
    ArrayList<Integer> slo = new ArrayList<>();

    private Arik() {
        bot = TelegramBotAdapter.build("400524449:AAHFddGoUjTo2fwAyDc-ocX927fb49Oahn0");
//        load_from_db();
    }

    // Get instance
    public static Arik getInstance() {
        if (arik == null) {
            arik = new Arik();
        }
        return arik;
    }

    public void close() {
        arik = null;
        bot = null;
    }

    private void load_from_db() {

        ResultSet rs = MySql.select("select * from sagiv.arik_accounts;");

        while (true) {
            try {
                if (!rs.next()) break;
                String name = rs.getString("name");
                int id = rs.getInt("id");
                accounts.add(id);

                // Slo
                if (name.toLowerCase().equals("sagiv") ||
                        name.toLowerCase().equals("yogi") ||
                        name.toLowerCase().equals("moti")) {
                    slo.add(id);
                }

                System.out.println(id);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }

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
    public void sendMessage(int id, String text, Keyboard keyBoard) {
        if (keyBoard != null) {
            getBot().execute(new SendMessage(id, text).replyMarkup(keyBoard));
            updateId += 1;
        } else {
            getBot().execute(new SendMessage(id, text));
            updateId += 1;
        }
    }

    // Send message
    public void sendMessageToEveryOne(String text) {
        try {
            for (int account : accounts) {
                sendMessage(account, text, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Send message
    public void sendMessageToSlo(String text) {
        try {
            for (int person : slo) {
                sendMessage(person, text, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------- Getters and Setters ---------- //

    public int getUpdateId() {
        return updateId;
    }

    public void setUpdateId(int updateId) {
        this.updateId = updateId;
    }

    public TelegramBot getBot() {
        return bot;
    }

    public void setBot(TelegramBot bot) {
        this.bot = bot;
    }

}
