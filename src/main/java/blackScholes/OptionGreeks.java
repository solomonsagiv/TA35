package blackScholes;


public class OptionGreeks {

    public double delta;
    public double theta;
    public double rho;
    public double gamma;
    public double vega;

    public String toString() {

        String out = "\n delta-[" + delta + "] \n theta-[" + theta;
        out += "] \n rho-[" + rho + "] \n gamma-[" + gamma;
        out += "] \n vega-[" + vega;

        return out;
    }

}

