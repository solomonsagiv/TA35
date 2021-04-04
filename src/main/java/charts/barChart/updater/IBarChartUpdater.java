package charts.barChart.updater;

import org.jfree.data.category.DefaultCategoryDataset;

public interface IBarChartUpdater {
	
	public void updateBasics(DefaultCategoryDataset dataset);
	public void update(DefaultCategoryDataset dataset);
	
}
