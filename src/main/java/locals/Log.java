package locals;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log {

	// Get logger
	public static Logger getLogger(String location, String name) {

		Logger logger = Logger.getLogger("MyLog");
		FileHandler fh;

		try {
			// This block configure the logger with handler and formatter
			fh = new FileHandler(location + name);
			logger.addHandler(fh);
			fh.setFormatter(new MyFormatter());

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return logger;
	}
}

class MyFormatter extends Formatter {
	// Create a DateFormat to format the logger timestamp.

	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder(1000);
		builder.append("[").append(record.getLevel()).append("] - ");
		builder.append(formatMessage(record));
		builder.append("\n");
		return builder.toString();
	}
}