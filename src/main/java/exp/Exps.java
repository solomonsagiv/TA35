package exp;

import api.BASE_CLIENT_OBJECT;
import api.TA35;
import dataBase.DataBaseHandler;
import java.util.ArrayList;

public class Exps {

    private ArrayList<Exp> exps_list;
    private ExpWeek week;
    private ExpMonth month;

    public Exps(BASE_CLIENT_OBJECT client) {
        this.exps_list = new ArrayList<>();
        week = new ExpWeek(client, DataBaseHandler.EXP_WEEK);
        month = new ExpMonth(client, DataBaseHandler.EXP_MONTH);
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

}
