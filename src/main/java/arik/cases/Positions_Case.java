package arik.cases;

import arik.Arik;
import arik.ArikCase;
import arik.locals.KeyBoards;
import com.pengrad.telegrambot.model.Update;

public class Positions_Case extends ArikCase {

    public Positions_Case(String name) {
        super(name);
        setKeyboard(KeyBoards.positions());
    }

    @Override
    public boolean doCase(Update update) {
        for (long id : Arik.slo) {
            if (update.message().from().id() == id) {
                Arik.getInstance().sendMessage(update, "Choose position", getKeyboard());
                break;
            }
        }
        return true;
    }
}