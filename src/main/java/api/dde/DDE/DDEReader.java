package api.dde.DDE;

import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;
import api.ApiObject;
import api.deltaTest.Calculator;
import counter.BackGroundRunner;
import counter.WindowTA35;
import exp.Exp;
import locals.L;
import miniStocks.MiniStock;
import options.Option;
import options.Options;
import options.Strike;
import threads.MyThread;

public class DDEReader {

    DDEConnection ddeConnection;
    private DataSheet dataSheet;
    private OptionsSheet optionsSheet;

    ApiObject apiObject;

    // Constructor
    public DDEReader(DDEConnection ddeConnection, ApiObject apiObject) {
        this.apiObject = apiObject;
        this.ddeConnection = ddeConnection;
    }

    // Start
    public void start() throws InterruptedException {
        dataSheet = new DataSheet(ddeConnection);
        dataSheet.startRunners();

        optionsSheet = new OptionsSheet();
    }

    // Close
    public void close() {
        dataSheet.closeRunners();
        optionsSheet.close();
    }
}

// Get ticker data from excel
class DataSheet {

    private String excelPath = "C://Users/yosef/Desktop/[TA35.xlsm]DDE";
    private boolean run = true;
    ApiObject apiObject = ApiObject.getInstance();
    DDEClientConversation conversation;
    DataSheetThread dataSheetThread;
    BasketRunner basketRunner;
    IndDeltaRunner indDeltaRunner;

    // Start runner
    public void startRunners() {

        dataSheetThread = new DataSheetThread();
        dataSheetThread.start();

        // Basket runner
        basketRunner = new BasketRunner();
        basketRunner.getHandler().start();

        // Ind delta runner
        indDeltaRunner = new IndDeltaRunner();
        indDeltaRunner.getHandler().start();
    }

    // Close runners
    public void closeRunners() {

        dataSheetThread.close();
        basketRunner.getHandler().close();
        indDeltaRunner.getHandler().close();
        try {
            conversation.disconnect();
        } catch (DDEException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Cells
    String futureCell = "R2C1";
    String indexCell = "R2C2";
    String indexBidCell = "R2C3";
    String indexAskCell = "R2C4";
    String openCell = "R2C5";
    String highCell = "R2C6";
    String lowCell = "R2C7";
    String baseCell = "R2C8";
    String lastCell = "R2C9";
    String futureWeekBidCell = "R9C2";
    String futureWeekAskCell = "R9C3";
    String futureWeekCell = "R9C4";
    //	String bigConBidCell = "R11C2";
//	String bigConAskCell = "R11C3";
    String statusCell = "R7C1";
    String futureBidCell = "R7C2";
    String futureAskCell = "R7C3";
    String daysToExpCell = "R4C1";

    // Constructor
    public DataSheet(DDEConnection ddeConnection) {
        this.conversation = ddeConnection.createNewConversation(excelPath);
    }

    // Basket class
    private class Basket {

        private boolean upDown;

        public Basket() {
        }

        public boolean isUpDown() {
            return upDown;
        }

        public void setUpDown(boolean upDown) {
            this.upDown = upDown;
        }
    }

    // Data sheet thread
    private class DataSheetThread extends Thread {

        @Override
        public void run() {

            while (run) {
                try {

                    // Update data to apiObject
                    update();

                    // Sleep
                    sleep(200);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void update() {
            try {

                String status = conversation.request(statusCell).replaceAll("\\s+", "");

                // Ticker datas
                apiObject.setStatus(status);

                if (apiObject.getStatus() != "preopen") {

                    Options optionsWeek = apiObject.getExpWeek().getOptions();
                    Options optionsMonth = apiObject.getExpMonth().getOptions();

                    apiObject.setDaysToExp(dbl(conversation.request(daysToExpCell)));
                    optionsMonth.setContract(dbl(conversation.request(futureCell)));

                    // Week
                    optionsWeek.setContract(dbl(conversation.request(futureWeekCell)));
                    optionsWeek.setContractBid(dbl(conversation.request(futureWeekBidCell)));
                    optionsWeek.setContractAsk(dbl(conversation.request(futureWeekAskCell)));

                    // Big
                    apiObject.setIndex(dbl(conversation.request(indexCell)));
                    apiObject.setIndBid(dbl(conversation.request(indexBidCell)));
                    apiObject.setIndAsk(dbl(conversation.request(indexAskCell)));
                    apiObject.setHigh(dbl(conversation.request(highCell)));
                    apiObject.setLow(dbl(conversation.request(lowCell)));
                    apiObject.setBase(dbl(conversation.request(baseCell)));
                    apiObject.setOpen(dbl(conversation.request(openCell)));
                    apiObject.setLast(dbl(conversation.request(lastCell)));
                    optionsMonth.setContractBid(dbl(conversation.request(futureBidCell)));
                    optionsMonth.setContractAsk(dbl(conversation.request(futureAskCell)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void close() {
            run = false;
        }

        // To double
        private double dbl(String s) {
            return Double.parseDouble(s);
        }
    }

    private class IndDeltaRunner extends MyThread implements Runnable {

        public IndDeltaRunner() {
            setRunnable(this);
        }

        @Override
        public void run() {

            initStocksName();

            while (isRun()) {
                try {

                    // Sleep
                    Thread.sleep(1000);

                    if (BackGroundRunner.streamMarketBool) {
                        // Calc
                        calcStocksDelta();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void initStocksName() {

            MiniStock[] miniStocks = apiObject.getStocksHandler().getStocks();

            for (int i = 2; i < 37; i++) {
                try {
                    MiniStock miniStock = miniStocks[i - 2];

                    String name = (L.str(conversation.request(cell(i, 17)))).replace("\r\n", "");
                    miniStock.setName(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        private void

        calcStocksDelta() {

            MiniStock[] miniStocks = apiObject.getStocksHandler().getStocks();

            for (int i = 2; i < 37; i++) {
                try {
                    MiniStock miniStock = miniStocks[i - 2];

//                    System.out.println(cell(i, 12));

                    int newLast = (int) L.dbl(conversation.request(cell(i, 12)));
                    int newVolume = (int) L.dbl(conversation.request(cell(i, 13)));
                    double bid = L.dbl(conversation.request(cell(i, 14)));
                    double ask = L.dbl(conversation.request(cell(i, 15)));
                    double weight = L.dbl(conversation.request(cell(i, 16)));
                    double delta = Calculator.calcMiniStockDelta(miniStock, newLast, newVolume);

                    miniStock.setLast(newLast);
                    miniStock.setBid(bid);
                    miniStock.setAsk(ask);
                    miniStock.setVolume(newVolume);
                    miniStock.setWeight(weight);
                    miniStock.appendDelta(delta);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        public String cell(int row, int col) {
            return "R" + row + "C" + col;
        }

        @Override
        public void initRunnable() {

        }
    }

    // Basket runner thread
    private class BasketRunner extends MyThread implements Runnable {

        double ind_0 = 0;

        public BasketRunner() {
            setRunnable(this);
        }

        @Override
        public void run() {

            while (isRun()) {
                try {

                    // Sleep
                    Thread.sleep(5000);

                    // Find basket
                    findBasket();

                    // Reset pre variables
                    resetVariables();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void resetVariables() {
            ind_0 = apiObject.getIndex();
        }

        private void findBasket() {

            MiniStock[] stocks = apiObject.getStocksHandler().getStocks();
            int changesCount = 0;

            // For each ministock
            for (MiniStock stock : stocks) {

                int volume = stock.getVolume();

                // Volume changeed
                if (volume > stock.getPreVolume() && stock.getVolume() != 0 && stock.getPreVolume() != 0) {
                    changesCount++;
                }

                // Update pre volume
                stock.setPreVolume(volume);
            }

            // More then 30 changes
            if (changesCount > 28) {

                double ind = apiObject.getIndex();

                // Up
                if (ind > ind_0) {
                    apiObject.incrementBasketUp();
                }

                // Down
                if (ind < ind_0) {
                    apiObject.incrementBasketDown();
                }
            }

            try {
                WindowTA35.log.setText("Changed: " + changesCount + " \n " + ind_0);
            } catch (Exception e) {

            }
        }

        @Override
        public void initRunnable() {
            setRunnable(this);
        }
    }
}

// Get ticker data from excel
class OptionsSheet {
    DDEConnection ddeConnection;
    ApiObject apiObject = ApiObject.getInstance();
    Calculator calculator;
    private String weekPath = "C://Users/yosef/Desktop/[TA35.xlsm]Import Week";
    private String monthPath = "C://Users/yosef/Desktop/[TA35.xlsm]Import Month";

    OptionsRunner weekRunner, monthRunner;

    public OptionsSheet() {
        this.ddeConnection = new DDEConnection(apiObject);
        this.calculator = new Calculator();

//        weekRunner = new OptionsRunner(apiObject.getExpWeek(), weekPath);
        monthRunner = new OptionsRunner(apiObject.getExpMonth(), monthPath);

//        weekRunner.getHandler().start();
        monthRunner.getHandler().start();
    }

    public void close() {
        weekRunner.close();
        monthRunner.close();
    }

    // Week
    class OptionsRunner extends MyThread implements Runnable {

        Exp exp;
        DDEClientConversation conversation;

        public OptionsRunner(Exp exp, String excelPath) {
            setRunnable(this);
            this.conversation = ddeConnection.createNewConversation(excelPath);
            this.exp = exp;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            // Set up options
            setUpOptions(exp, conversation);

            while (isRun()) {
                try {
                    // Update data
                    update();

                    // Sleep
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    close();
                    break;
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

            bidPrice1 = requestInt(cell(row, 2));
            askPrice1 = requestInt(cell(row, 3));
            last = requestInt(cell(row, 4));
            volume = requestInt(cell(row, 5));
            delta = requestDouble(cell(row, 6));

            if (apiObject.getStatus().contains("stream") && apiObject.isDbLoaded()) {
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
            option.setDelta(delta);
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

                    Option call = new Option("c", strike);
                    Option put = new Option("p", strike);

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

        public void close() {
            try {
                conversation.disconnect();
                getHandler().close();
            } catch (DDEException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void initRunnable() {
            setRunnable(this);
        }
    }

    // To double
    public double dbl(String s) {
        return Double.parseDouble(s);
    }

    public String cell(int row, int col) {
        return "R" + row + "C" + col;
    }

}
