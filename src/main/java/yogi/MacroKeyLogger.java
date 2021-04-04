package yogi;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

public class MacroKeyLogger implements NativeKeyListener, NativeMouseListener {
	Robot robot;

	Macros macro;

	Yogi window;

	public MacroKeyLogger(Yogi window) {
		this.window = window;
		macro = new Macros();
	}

	// Key
	public void nativeKeyReleased(NativeKeyEvent key) {
		macro.setKey(NativeKeyEvent.getKeyText(key.getKeyCode()));
		window.textAreaLastMacro.setText(macro.toString());
	}

	// Mouse
	public void nativeMouseReleased(NativeMouseEvent e) {
		
		if (macro.getKey() == null) {
			macro.setPoint(new int[] { e.getX(), e.getY() });
		}
		
		window.textAreaLastMacro.setText(macro.toString());
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
			logger.setLevel(Level.OFF);

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

	public void resetMacro() {
		macro = new Macros();
		window.textAreaLastMacro.setText(macro.toString());
	}
	
	public void appendMacro() {
		if (macro.getKey() != null && macro.getPoint() != null) {
			Locals.keysMap.put(macro.getKey(), macro);
			appendMacroListToTextArea();
			resetMacro();
		}
	}

	public void appendMacroListToTextArea() {
		window.textAreaAllMacros.setText(null);
		for (Entry<String, Macros> entry : Locals.keysMap.entrySet()) {
			window.textAreaAllMacros.append(entry.getValue().toString() + "\n");
		}
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
