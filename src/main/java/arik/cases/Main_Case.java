package arik.cases;

import arik.Arik;
import arik.ArikCase;
import arik.locals.KeyBoards;
import com.pengrad.telegrambot.model.Update;

public class Main_Case extends ArikCase {

    public Main_Case(String name) {
        super(name);
        setKeyboard(KeyBoards.main());
    }

    @Override
    public boolean doCase(Update update) {
        Arik.getInstance().sendMessage(update, "Please choose somthing...", getKeyboard());
        return true;
    }
}