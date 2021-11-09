package arik;

import service.MyBaseService;

public class AlertsHandler extends MyBaseService {

    public static void main(String[] args) {
        Arik.getInstance().sendMessageToEveryOne("Test");
        Arik.getInstance().close();
    }

    boolean LONG = false;
    boolean SHORT = false;
    int target_price = 100000;

    public AlertsHandler() {
        super();
    }

    @Override
    public void go() {
        // --------------- LONG --------------- //
        // Enter long
        if (!LONG) {
            if (apiObject.getV5() > target_price && apiObject.getV6() > target_price) {
                LONG = true;
                Arik.getInstance().sendMessageToEveryOne("LONG \n" + apiObject.getName() + " " + apiObject.getIndex());
            }
        }

        // --------------- SHORT --------------- //
        // Enter short
        if (!SHORT) {
            if (apiObject.getV5() < target_price * -1 && apiObject.getV6() < target_price * -1) {
                SHORT = true;
                Arik.getInstance().sendMessageToEveryOne("SHORT \n" + apiObject.getName() + " " + apiObject.getIndex());
            }
        }
    }

    @Override
    public String getName() {
        return "Alert service";
    }

    @Override
    public int getSleep() {
        return 10000;
    }
}
