package options;

import api.BASE_CLIENT_OBJECT;
import myJson.IJsonData;
import myJson.JsonStrings;
import myJson.MyJson;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class Options implements IJsonData {

    public static final int MONTH = 1;
    public static final int WEEK  = 0;

    private double contract = 0.0;
    private double contractBid = 0.0;
    private double contractAsk = 0.0;

    // --- Collections ---
    private final List<Strike> strikes;                       // לשמירה על סדר/איטרציה
    private final Map<Double, Strike> strikesByPrice;         // גישה מהירה לפי סטרייק
    private final Map<String, Option> optionsByName;          // גישה לפי שם (למשל "C1875")
    private final Map<Integer, Option> optionsById;           // גישה לפי מזהה (אם קיים)
    private final List<Option> optionsList;                   // שמירת סדר הכנסת אופציות (אם חשוב)

    // --- Internal state ---
    private int bidAskCounter = 0;

    protected BASE_CLIENT_OBJECT client;

    public Options(BASE_CLIENT_OBJECT client) {
        this.client = client;
        this.strikes = new ArrayList<>();
        this.strikesByPrice = new HashMap<>();
        this.optionsByName = new HashMap<>();
        this.optionsById = new HashMap<>();
        this.optionsList = new ArrayList<>();
    }

    /* ============================== Option Access ============================== */

    /** קבלת אופציה לפי שם בפורמט "C1875" או "P1900" */
    public Option getOption(String name) {
        if (name == null || name.length() < 2) return null;
        // נסה קודם מהמפה
        Option direct = optionsByName.get(name);
        if (direct != null) return direct;

        // פרסינג fallback
        char sideChar = Character.toUpperCase(name.charAt(0));
        String strikeStr = name.substring(1).trim();
        double targetStrike;
        try {
            targetStrike = Double.parseDouble(strikeStr);
        } catch (NumberFormatException e) {
            return null;
        }
        Strike strike = strikesByPrice.get(targetStrike);
        if (strike == null) return null;
        return (sideChar == 'C') ? strike.getCall() : strike.getPut();
    }

    /** קבלת אופציה לפי צד וסטרייק */
    public Option getOption(Option.Side side, double targetStrike) {
        Strike strike = strikesByPrice.get(targetStrike);
        if (strike == null) return null;
        return (side == Option.Side.CALL) ? strike.getCall() : strike.getPut();
    }

    /** לפי ID (אם קיים) */
    public Option getOptionById(int id) {
        return optionsById.get(id);
    }

    /* ============================== Strike Access ============================== */

    /** קבלת Strike לפי מחיר סטרייק */
    public Strike getStrike(double strikePrice) {
        return strikesByPrice.get(strikePrice);
    }

    /** רשימת מחירי סטרייקים (לא ממוינת) */
    public ArrayList<Double> getStrikePricesList() {
        return new ArrayList<>(strikesByPrice.keySet());
    }

    /** הוספת סטרייק (אם לא קיים) */
    public void addStrike(Strike strike) {
        if (strike == null) return;
        double k = strike.getStrike();
        if (!strikesByPrice.containsKey(k)) {
            strikesByPrice.put(k, strike);
            strikes.add(strike);
        }
    }

    /** הסרת סטרייק לפי מחיר */
    public void removeStrike(double strikePrice) {
        Strike s = strikesByPrice.remove(strikePrice);
        if (s != null) {
            strikes.remove(s);
        }
    }

    /** הסרת סטרייק לפי מופע */
    public void removeStrike(Strike strike) {
        if (strike == null) return;
        strikesByPrice.remove(strike.getStrike());
        strikes.remove(strike);
    }

    /* ============================== Mutations ============================== */

    /** רישום אופציה במבנים (שם/ID/סטרייק) והלבשה על Strike */
    public void setOption(Option option) {
        if (option == null) return;

        // מפות לפי שם ו־ID
        if (option.getName() != null) {
            optionsByName.put(option.getName(), option);
        }
        Integer oid = option.getId(); // בגרסה שלי זה Integer; אם אצלך int, זה עדיין יעבוד ע"י auto-boxing
        if (oid != null && oid > 0) {
            optionsById.put(oid, option);
        }
        optionsList.add(option);

        // שידוך לסטרייק המתאים
        double k = option.getStrike();
        Strike strike = strikesByPrice.get(k);
        if (strike == null) {
            strike = new Strike();
            strike.setStrike(k);
            strikesByPrice.put(k, strike);
            strikes.add(strike);
        }

        Option.Side side = option.getSideEnum();
        boolean isCall = (side == Option.Side.CALL) ||
                (side == null && (option.getSide() != null && option.getSide().equalsIgnoreCase("C")));

        if (isCall) {
            if (strike.getCall() == null) strike.setCall(option);
        } else {
            if (strike.getPut() == null) strike.setPut(option);
        }
    }

    /** טעינת נתוני אופציות מ־MyJson: מפתח = שם אופציה */
    public void load_options_data_from_json(MyJson json) {
        for (String key : json.keySet()) {
            try {
                MyJson jsonOption = json.getMyJson(key);
                Option option = optionsByName.get(key);
                if (option != null) {
                    option.loadFromJson(jsonOption);
                } // else: אפשר לוג אזהרה
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* ============================== Serialization ============================== */

    /** JSON בסיסי של הקונטרקט + דאטה של כל האופציות */
    public JSONObject getOptionsWithDataAsJson() {
        JSONObject json = getAsJson(); // MyJson כנראה יורש/מתנהג כ-JSONObject בפרויקט שלך
        json.put(JsonStrings.data.toString(), getData());
        return json;
    }

    /** מחזיר MyJson עם כל האופציות: key=name, value=option.getAsJson() */
    public MyJson getData() {
        MyJson json = new MyJson();
        for (Option option : optionsList) {
            if (option == null || option.getName() == null) continue;
            json.put(option.getName(), option.getAsJson());
        }
        return json;
    }

    @Override
    public MyJson getAsJson() {
        MyJson json = new MyJson();
        json.put(JsonStrings.con, getContract());
        json.put("contractBid", getContractBid());
        json.put("contractAsk", getContractAsk());
        json.put("bidAskCounter", getBidAskCounter());
        return json;
    }

    @Override
    public void loadFromJson(MyJson json) {
        // אם תרצה לטעון חזרה contract/bid/ask — אפשר להוסיף כאן
    }

    @Override
    public MyJson getResetJson() {
        return new MyJson();
    }

    @Override
    public MyJson getFullResetJson() {
        return new MyJson();
    }

    /* ============================== Presentation ============================== */

    /** הדפסה אנכית של ה־Strikes, ממוינת לפי סטרייק */
    public String toStringVertical() {
        StringBuilder sb = new StringBuilder();
        List<Strike> sorted = strikes.stream()
                .sorted(Comparator.comparingDouble(Strike::getStrike))
                .collect(Collectors.toList());
        for (Strike s : sorted) {
            sb.append(s.toString()).append("\n\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Options{contractBid=" + contractBid +
                ", contractAsk=" + contractAsk +
                ", contract=" + contract +
                ", bidAskCounter=" + bidAskCounter +
                ", strikes=" + strikes.size() +
                ", options=" + optionsList.size() +
                "}";
    }

    /* ============================== Contract & Ticks ============================== */

    public void setContractBid(double newBid) {
        if (this.contractBid != 0.0 && newBid > this.contractBid) {
            bidAskCounter++;
        }
        this.contractBid = newBid;
    }

    public double getContractBid() { return contractBid; }

    public void setContractAsk(double newAsk) {
        if (this.contractAsk != 0.0 && newAsk < this.contractAsk) {
            bidAskCounter--;
        }
        this.contractAsk = newAsk;
    }

    public double getContractAsk() { return contractAsk; }

    public double getContract() { return contract; }
    public void setContract(double contract) { this.contract = contract; }

    public int getBidAskCounter() { return bidAskCounter; }
    public void setBidAskCounter(int bidAskCounter) { this.bidAskCounter = bidAskCounter; }

    /* ============================== Bulk Getters/Setters ============================== */

    public List<Strike> getStrikes() { return strikes; }

    /** החלפה מלאה של רשימת סטרייקים (תנקה גם את המפה) */
    public void setStrikes(List<Strike> newStrikes) {
        strikes.clear();
        strikesByPrice.clear();
        if (newStrikes != null) {
            for (Strike s : newStrikes) {
                if (s == null) continue;
                strikes.add(s);
                strikesByPrice.put(s.getStrike(), s);
            }
        }
    }

    public List<Option> getOptionsList() { return optionsList; }
    public Map<String, Option> getOptionsByName() { return optionsByName; }
    public Map<Integer, Option> getOptionsById() { return optionsById; }

    /* ============================== Utilities ============================== */

    /** מחזיר סטרייק ה‑ATM הקרוב ביותר למחיר נתון */
    public Strike getAtmStrike(double underlying) {
        if (strikes.isEmpty()) return null;
        return strikes.stream()
                .min(Comparator.comparingDouble(s -> Math.abs(s.getStrike() - underlying)))
                .orElse(null);
    }
}
