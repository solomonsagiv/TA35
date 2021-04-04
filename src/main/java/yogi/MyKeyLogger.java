package yogi;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JTextField;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

public class MyKeyLogger implements NativeKeyListener, NativeMouseListener {
	Robot robot;

	JTextField mouse_x;
	JTextField mouse_y;

	int[] start_point = new int[2];
	int[] minus = { 445, 685 };
	int[] plus = { 445, 670 };
	int[] accept = { 445, 720 };
	int[] shaldag = { 35, 17 };
	int[] shaldag_user_name = { 580, 375 };
	int[] shaldag_password = { 580, 412 };
	int[] shaldag_accept = { 753, 536 };
	int[] nigzarim = { 1201, 27 };
	int[] nigzari_maof = { 1180, 46 };
	int[] choose_account = { 1193, 911 };
	int[] account = { 1181, 825 };
	int[] sell = { 772, 767 };
	int[] buy = { 702, 767 };
	int[] setting = { 722, 28 };
	int[] setting_maof = { 720, 47 };
	int[] save = { 236, 279 };
	int[] exit_setting = { 1223, 17 };

	// Constructor
	public MyKeyLogger(JTextField mouse_x, JTextField mouse_y) {
		this.mouse_x = mouse_x;
		this.mouse_y = mouse_y;
	}

	// Key
	public void nativeKeyReleased(NativeKeyEvent key) {

		String keyCode = NativeKeyEvent.getKeyText(key.getKeyCode());

		// Print to consolek
		// System.out.println("Key Released: " +
		// NativeKeyEvent.getKeyText(key.getKeyCode()));
		
		// Plus
		if (keyCode.equals("F6")) {
			start_point[0] = MouseInfo.getPointerInfo().getLocation().x;
			start_point[1] = MouseInfo.getPointerInfo().getLocation().y;

			move_and_click(plus);
			move_and_click(accept);
			move(start_point);
		}

		// Minus
		if (keyCode.equals("F5")) {
			start_point[0] = MouseInfo.getPointerInfo().getLocation().x;
			start_point[1] = MouseInfo.getPointerInfo().getLocation().y;

			move_and_click(minus);
			move_and_click(accept);
			move(start_point);
		}

		// Init macro buttons
		initMacroButtons(keyCode);
	}

	private void initMacroButtons(String keyCode) {
		int[] point = Locals.keysMap.get(keyCode).getPoint();

		if (point != null) {
			move_and_click(point);
		}
	}

	// Mouse
	public void nativeMouseReleased(NativeMouseEvent e) {
		mouse_x.setText(str(e.getX()));
		mouse_y.setText(str(e.getY()));
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void nativeKeyPressed(NativeKeyEvent e) {

		if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
			try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void nativeMouseClicked(NativeKeyEvent e) {
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent e) {

	}

	@Override
	public void nativeMousePressed(NativeMouseEvent e) {
	}

	// Start listening
	public void start() throws AWTException {
		try {
			robot = new Robot();

			// Clear previous logging configurations.4gnccccc
			LogManager.getLogManager().reset();

			// Get the logger for "org.jnativehook" and set the level to off.
			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
			logger.setLevel(Level.ALL);

			GlobalScreen.registerNativeHook();

		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(0);
		}

		GlobalScreen.addNativeKeyListener(this);
		GlobalScreen.addNativeMouseListener(this);

	}

	// Move and click
	public void move_and_click(int[] point) {
		robot.mouseMove(point[0], point[1]);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	// Click and release
	private void click_release(int keyEvent) {
		robot.keyPress(keyEvent);
		robot.keyRelease(keyEvent);
	}

	// Move and click
	public void move_and_double_click(int[] point) {
		robot.mouseMove(point[0], point[1]);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	// Move and click
	public void move(int[] point) {
		robot.mouseMove(point[0], point[1]);
	}

	// Stop
	public void stop() {
		try {
			GlobalScreen.removeNativeKeyListener(this);
			GlobalScreen.removeNativeMouseListener(this);
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}
	}

	// String
	private String str(Object o) {
		return String.valueOf(o);
	}

}