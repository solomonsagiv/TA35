package logger;

import java.io.IOException;
import java.time.LocalTime;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class MyLogger {

	static Logger logger;

	// Get instance
	public static synchronized Logger getInstance() {
		if (logger == null) {
			logger = createLogger();
		}
		return logger;
	}

	// Constructor
	private MyLogger() {}

	// Create the logger file
	public static Logger createLogger() {
		String location = "C:/Users/user/Desktop/";
		String name = "TA35_logger.txt";
		

		Logger logger = Logger.getLogger(name);
		FileHandler fh;

		try {
			// This block configure the logger with handler and formatter
			fh = new FileHandler(location + name);
			logger.addHandler(fh);
			BriefFormatter briefFormatter = new BriefFormatter();
			fh.setFormatter(briefFormatter);

			return logger;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return logger;
	}

}

 class BriefFormatter extends Formatter {
	 String lineSeparator = System.getProperty("line.separator");
	 
	public BriefFormatter() {
		super();
	}

	@Override
	public String format(final LogRecord record) {
		return LocalTime.now() + " - " + record.getMessage() + lineSeparator;
	}
}
