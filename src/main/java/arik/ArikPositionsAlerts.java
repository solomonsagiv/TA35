package arik;

import service.MyBaseService;

import java.util.ArrayList;

public class ArikPositionsAlerts extends MyBaseService {

    public static void main(String[] args) {
        Arik.getInstance().sendMessageToEveryOne("Test");
        Arik.getInstance().close();
    }

    boolean LONG = false;
    boolean SHORT = false;
    int target_price = 100000;

    ArrayList<Double> targets = new ArrayList<>();

    public ArikPositionsAlerts() {
        super();
        targets.add(30000.0);
        targets.add(60000.0);
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

        // --------------- Alert for every time passing midpoint --------------- //
        for (double target : targets) {
            double target_minus = target *-1;

            // +
            if ((apiObject.getPre_v5() < target && apiObject.getV5() > target) || (apiObject.getPre_v5() > target && apiObject.getV5() < target)) {
                Arik.getInstance().sendMessageToSlo("V5 = " + apiObject.getV5());
            }

            // -
            if ((apiObject.getPre_v5() < target_minus && apiObject.getV5() > target_minus) || (apiObject.getPre_v5() > target_minus && apiObject.getV5() < target_minus)) {
                Arik.getInstance().sendMessageToSlo("V5 = " + apiObject.getV5());
            }

            // +
            if ((apiObject.getPre_v6() < target && apiObject.getV6() > target) || (apiObject.getPre_v6() > target && apiObject.getV6() < target)) {
                Arik.getInstance().sendMessageToSlo("V6 = " + apiObject.getV6());
            }

            // -
            if ((apiObject.getPre_v6() < target_minus && apiObject.getV6() > target_minus) || (apiObject.getPre_v6() > target_minus && apiObject.getV6() < target_minus)) {
                Arik.getInstance().sendMessageToSlo("V6 = " + apiObject.getV6());
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
