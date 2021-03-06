package vulc.luag;

import java.io.File;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public abstract class LoggerSetup {

	public static final void setup(Logger logger, String folder) {
		logger.setLevel(Level.ALL);
		Locale.setDefault(Locale.ENGLISH);
		try {
			// fallback value, in case a good filename is not found
			String fileName = folder + "luag.log";

			for(int i = 0; i < 100; i++) {
				// first, try with "luag.log" then try adding numbers
				fileName = folder + "luag" + (i == 0 ? "" : "-" + i) + ".log";
				File file = new File(fileName);

				// if does not exist or is a file and is not locked
				if(!file.exists() || (file.isFile() && file.delete())) {
					break;
				}
			}

			Console.logFile = fileName;
			FileHandler fh = new FileHandler(fileName);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
		} catch(Exception e) {
			e.printStackTrace();
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.info("Shutting down Console");
			}
		});
	}

}
