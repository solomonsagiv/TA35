package TA_35.ta_35;

import org.json.JSONObject;
import org.jsoup.Jsoup;

public class TA35ClientObject {
	
	static String apiURL = "";
	
	public static JSONObject getData() {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(Jsoup.connect(apiURL).get().select("body").text());
		} catch (Exception e) {
			System.out.println("Error in my api, " + e.getMessage());
		}
		return jsonObject;
	}

}
