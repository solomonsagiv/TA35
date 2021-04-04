package lists;

import java.util.HashMap;
import java.util.Map;

public class ListsHandler {
	
	private Map< String, MyChartList> map = new HashMap<>();
	
	public Map<String, MyChartList> getMap() {
		return map;
	}
	
	public void appendList(String mapName, MyChartList myChartList) {
		map.put(mapName, myChartList);
	}
}
