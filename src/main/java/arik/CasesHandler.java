package arik;

import java.util.ArrayList;

public class CasesHandler {

    private ArrayList<ArikCase> cases = new ArrayList<>();

    public void addCase(ArikCase arikCase) {
        cases.add(arikCase);
    }

    public void removeCase(ArikCase arikCase) {
        cases.remove(arikCase);
    }

    public ArrayList<ArikCase> getCases() {
        return cases;
    }

}
