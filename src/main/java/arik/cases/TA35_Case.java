package arik.cases;

import arik.Arik;
import arik.ArikCase;
import arik.dataHandler.DataHandler;
import arik.dataHandler.DataObject;
import arik.locals.KeyBoards;
import com.pengrad.telegrambot.model.Update;
import locals.L;

public class TA35_Case extends ArikCase {

    public TA35_Case(String name) {
        super(name);
        setKeyboard(KeyBoards.main());
    }

    @Override
    public boolean doCase(Update update) {

        DataHandler dataHandler = Arik.getInstance().getDataHandler();

        DataObject index = dataHandler.get(DataHandler.TA35_INDEX);
        DataObject df_5 = dataHandler.get(DataHandler.TA35_DF_5);
        DataObject df_6 = dataHandler.get(DataHandler.TA35_DF_6);


        String return_text = "TA35" + " price " + L.format100(index.getValue()) + "\n" +
                "DF 5 = " + L.format_int(df_5.getValue()) + "\n" +
                "DF 6 = " + L.format_int(df_6.getValue()) + "\n";

        Arik.getInstance().sendMessage(update, return_text, getKeyboard());
        return true;
    }
}