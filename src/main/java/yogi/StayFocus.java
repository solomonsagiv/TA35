package yogi;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.time.LocalTime;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class StayFocus extends Thread {

	boolean run = true;
	JFrame frame;
	LocalTime now;
	JTextField time_to_move;
	static Robot robot;
	
	int min = 30;

	// Constructor
	public StayFocus(JFrame frame, JTextField time_to_move) throws AWTException {
		this.frame = frame;
		this.time_to_move = time_to_move;
		this.robot = new Robot();
	}

	@Override
	public void run() {

		while (true) {
			try {
				// Initiate now
				now = LocalTime.now();
				if (now.getMinute() < min) {
					time_to_move.setText(String.valueOf(LocalTime.now().until(LocalTime.of(LocalTime.now().getHour(), min), SECONDS)));
				} else {
					time_to_move.setText(String.valueOf(LocalTime.now().until(LocalTime.of(LocalTime.now().getHour() + 1, 0), SECONDS)));
				}
				
				// Move
				if (now.getMinute() == min && now.getSecond() == 0 || now.getMinute() == 0 && now.getSecond() == 0) {
					move();
				}

				sleep(1000);
			} catch (InterruptedException e) {
				run = false;
			}
		}
	}

	// Move
	public static void move() {
		robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x + 3, MouseInfo.getPointerInfo().getLocation().y);
	}
}
