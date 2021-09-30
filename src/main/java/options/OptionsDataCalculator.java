package options;

import api.ApiObject;
import blackScholes.MyBlackScholes;
import service.MyBaseService;

public class OptionsDataCalculator extends MyBaseService {

    ApiObject apiObject;

    Options optionsMonth;
    Options optionsWeek;

    public OptionsDataCalculator() {
        apiObject = ApiObject.getInstance();
        optionsMonth = apiObject.getExpMonth().getOptions();
        optionsWeek = apiObject.getExpWeek().getOptions();
    }

    @Override
    public void go() {
        // Bid ask counter
        calculators();

        // StDev
        calculateStDev();
    }

    @Override
    public String getName() {
        return "Options black and sholds calc";
    }

    @Override
    public int getSleep() {
        return 500;
    }

    private void calculateStDev() {
        for (Option option : optionsMonth.getOptions_list()) {
            if (option.getStDev() == 0) {
                initStDev(option);
            }
            updateStDev(option);
        }
    }

    private void updateStDev(Option option) {

        boolean callPut;

        if (option.getSide().contains("c")) {
            callPut = true;
        } else {
            callPut = false;
        }

        double[] data = MyBlackScholes.getGreeaks(option.getStrike(), callPut,
                optionsMonth.getContract(), apiObject.getInterest(), apiObject.getDaysToExp(),
                option.getStDev());

        option.setCalcPrice(data[0]);
    }

    private void initStDev(Option option) {
        try {
            double[] arr;

            if (option.getSide().contains("c")) {
                if (option.getStrike() > optionsMonth.getContract()) {
                    arr = MyBlackScholes.getStDev(option);

                    // Update stDev
                    option.setStDev(arr[4]);
                    Option opositeOption = optionsMonth.getOption("p" + option.getStrike());
                    opositeOption.setStDev(arr[4]);
                }
            } else {
                if (option.getStrike() <= optionsMonth.getContract()) {
                    arr = MyBlackScholes.getStDev(option);

                    // Update stDev
                    option.setStDev(arr[4]);
                    Option opositeOption = optionsMonth.getOption("c" + option.getStrike());
                    opositeOption.setStDev(arr[4]);
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    double margin = 35;

    private void calculators() {
        // For each option
        for (Option option : optionsMonth.getOptions_list()) {
            try {
                // Bid Ask Counter
                bdCounter(option);

                // Delta counter
                deltaCounter(option);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void bdCounter(Option option) {
        // Bid
        if (option.getBidStateList().size() >= 2) {
            // Bid
            if (option.lastBid() > option.pretBid()) {
                option.setBidAskCounter(option.getBidAskCounter() + 1);
                option.addBidState(option.lastBid());
            }

            // Ask
            if (option.getAskStateList().size() >= 2) {
                // Ask
                if (option.lastAsk() < option.preAsk()) {
                    option.setBidAskCounter(option.getBidAskCounter() - 1);
                    option.addAskState(option.lastAsk());
                }
            }
            option.getBidAskCounterList().add(option.getBidAskCounter());
        }
    }
    
    private void deltaCounter(Option option) {
        double preAvg = (option.pretBid() + option.preAsk()) / 2;
        // Bigger
        if (option.getLast() > preAvg) {
            option.increaseCalcCounter();
        }

        // Smaller
        if (option.getLast() < preAvg) {
            option.decreaseCalcCounter();
        }
    }
}