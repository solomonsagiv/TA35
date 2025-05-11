package arik.cases;

import arik.Arik;
import arik.ArikCase;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

public class ChooseAlertCase extends ArikCase {

    Keyboard keyboard = new ReplyKeyboardMarkup(new KeyboardButton[]{new KeyboardButton("/Acc+-0"),
            new KeyboardButton("/Speed+-5000")});

    // Constructor
    public ChooseAlertCase(String name) {
        super(name);
        setKeyboard(keyboard);
    }

    @Override
    public boolean doCase(Update update) {
        Arik.getInstance().sendMessage(update, "Choose alert", getKeyboard());
        return true;
    }

}