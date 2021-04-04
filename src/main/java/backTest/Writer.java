package backTest;

import javax.swing.JTextField;

public class Writer {

	// Write to JText field
	public void write(Object data, JTextField textField) {

		String stringData = String.valueOf(data);
		textField.setText(stringData);
		
	}

}
