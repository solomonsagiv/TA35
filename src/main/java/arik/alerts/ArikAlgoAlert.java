package arik.alerts;

import java.util.ArrayList;

public abstract class ArikAlgoAlert {

    protected boolean LONG = false;
    protected boolean SHORT = false;
    protected boolean POSITION = false;

    protected double target_price_for_position;
    protected double target_price_for_exit_position;
    protected ArrayList<Double> targets;

    public ArikAlgoAlert(double target_price_for_position) {
        this.target_price_for_position = target_price_for_position;
    }

    public abstract void go();


}
