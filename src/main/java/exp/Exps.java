package exp;

import api.ApiObject;
import dataBase.DataBaseHandler;
import java.util.ArrayList;

public class Exps {

    private ArrayList<Exp> exps_list;
    private ExpWeek week;
    private ExpMonth month;

    public Exps(ApiObject apiObject) {
        this.exps_list = new ArrayList<>();
        week = new ExpWeek(apiObject, DataBaseHandler.EXP_WEEK);
        month = new ExpMonth(apiObject, DataBaseHandler.EXP_MONTH);
        exps_list.add(week);
        exps_list.add(month);
    }

    public ArrayList<Exp> getExps_list() {
        return exps_list;
    }

    public ExpWeek getWeek() {
        return week;
    }

    public ExpMonth getMonth() {
        return month;
    }

    public double get_delta() {
        return week.getOptions().getTotal_delta() + month.getOptions().getTotal_delta();
    }
}
