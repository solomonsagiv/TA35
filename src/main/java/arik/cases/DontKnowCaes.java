package arik.cases;

import arik.Arik;
import arik.ArikCase;
import arik.locals.KeyBoards;
import com.pengrad.telegrambot.model.Update;

public class DontKnowCaes extends ArikCase {

    public DontKnowCaes(String name) {
        super(name);
        setKeyboard(KeyBoards.main());
    }

    @Override
    public boolean doCase(Update update) {
        Arik.getInstance().sendMessage(update, "Don't know what to do", getKeyboard());
        return true;
    }
}
