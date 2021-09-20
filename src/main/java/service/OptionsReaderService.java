package service;

import api.ApiObject;
import api.dde.DDE.DDEConnection;
import api.deltaTest.Calculator;
import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;
import dataBase.mySql.Queries;
import exp.Exp;
import locals.L;
import options.Option;
import options.Options;
import options.Strike;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OptionsReaderService extends MyBaseService {

    DDEConnection ddeConnection;
    ApiObject apiObject = ApiObject.getInstance();
    Calculator calculator;
    Exp exp;
    DDEClientConversation conversation;

    public OptionsReaderService(Exp exp, String excelPath) {
        super();
        this.ddeConnection = new DDEConnection(apiObject);
        this.conversation = ddeConnection.createNewConversation(excelPath);
        this.calculator = new Calculator();
        this.exp = exp;
        setUpOptions(exp, conversation);
        load_options_status();
    }

    private void load_options_status() {
        ResultSet rs = Queries.get_options_status(exp.getExp_name());
        while (true) {
            try {
                if (!rs.next()) break;
                JSONObject json = (JSONObject) rs.getObject("value");
                exp.getOptions().load_options_data(json);
                break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private void update() {

        // Options
        for (Strike strike : exp.getOptions().getStrikes()) {
            try {
                updateOptionData(strike.getCall(), exp);
                updateOptionData(strike.getPut(), exp);
            } catch (DDEException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updateOptionData(Option option, Exp exp) throws DDEException {
        int row = option.getCellRow();

        int bidPrice1 = 0;
        int askPrice1 = 0;
        int last = 0;
        int volume = 0;
        double delta = 0;
        int open_positions = 0;

        bidPrice1 = requestInt(cell(row, 2));
        askPrice1 = requestInt(cell(row, 3));
        last = requestInt(cell(row, 4));
        volume = requestInt(cell(row, 5));
        delta = requestDouble(cell(row, 6));
        open_positions = (int) requestDouble(cell(row, 9));

        if (apiObject.isDbLoaded()) {
            // Calc
            calculator.calc(exp.getOptions(), option, last, volume, delta);
        }

        if (last != 0) {
            option.setLast(last);
        }

        option.setBid(bidPrice1);
        option.setAsk(askPrice1);
        option.getLastList().add((bidPrice1 + askPrice1) / 2);
        option.addBidState(bidPrice1);
        option.addAskState(askPrice1);
        option.setVolume(volume);
        option.appendDelta(delta);
        option.setOpen_pos(open_positions);
    }


    private void setUpOptions(Exp exp, DDEClientConversation conversation) {
        try {

            Options options = exp.getOptions();

            // Wait for future to update
            do {
                Thread.sleep(1000);
            } while (options.getContract() == 0);

            double currentFuture = options.getContract();

            int future0 = (((int) (currentFuture / 10)) * 10) - 100;

            // Update strikes
            exp.setFutureStartStrike(future0);
            exp.setFutureEndStrike(future0 + 200);

            String cell = "R%sC%s";

            // Update the options map
            for (int strike = future0; strike <= future0 + 200; strike += 10) {

                Option call = new Option("c", strike, options);
                Option put = new Option("p", strike, options);

                // Get the option cell
                for (int row = 1; row < 150; row++) {
                    String currentStrike = conversation.request(String.format(cell, row, 7));
                    if (currentStrike.contains(String.valueOf(strike))) {
                        call.setCellRow(row);
                        put.setCellRow(row + 1);
                    }
                }

                options.setOption(call);
                options.setOption(put);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String cell(int row, int col) {
        return "R" + row + "C" + col;
    }


    public double requestDouble(String cell) throws DDEException {
        try {
            return L.dbl(conversation.request(cell).replaceAll("\\s+", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public int requestInt(String cell) throws DDEException {
        try {
            return L.INT(conversation.request(cell).replaceAll("\\s+", ""));
        } catch (Exception e) {
//                e.printStackTrace();
            return 0;
        }
    }

    public String reques(String cell) throws DDEException {
        return conversation.request(cell).replaceAll("\\s+", "");
    }

    @Override
    public void go() {
        update();
    }

    @Override
    public String getName() {
        return "Options " + hashCode() + " Service";
    }

    @Override
    public int getSleep() {
        return 300;
    }
}
