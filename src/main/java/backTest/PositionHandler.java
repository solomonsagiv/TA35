package backTest;

import java.util.ArrayList;

public class PositionHandler {

	ArrayList<PositionBackTest> positions;

	public PositionHandler() {

		positions = new ArrayList<>();

	}

	public void addPosition(PositionBackTest position) {

		positions.add(position);

	}

	public double getATotalPnl() {

		double totalPnl = 0;

		for (int i = 0; i < positions.size(); i++) {

			PositionBackTest position = positions.get(i);

			if (position.getClosePrice() > 1000 && position.getStatus() == 2) {

				totalPnl += position.getPnl();
			} else {
				System.out.println("Else: " + position);
			}

		}

		return totalPnl;
	}

	public PositionBackTest getLastPosition() {

		return positions.get(positions.size() - 1);

	}

	public void printPositions() {

		for (PositionBackTest position : positions) {

			System.out.println(position);

		}

	}
	
	
	public double getAvgPnl() {
		
		double avg = 0;
		
		for (int i = 0; i < positions.size(); i++) {

			PositionBackTest position = positions.get(i);
			avg += position.getPnl();
			
		}
		avg /= positions.size();
		return avg;
	}

}
