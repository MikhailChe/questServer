package quest.controller.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class Logger {
	static volatile Logger	instance;

	BufferedWriter			errorLog;
	BufferedWriter			warningLog;
	BufferedWriter			messageLog;

	boolean					toConsole	= true;

	public static Logger inst() {
		if (instance == null) {
			synchronized (Logger.class) {
				if (instance == null) {
					instance = new Logger();
				}
			}
		}
		return instance;
	}

	public Logger() {
		LocalDateTime ldt = LocalDateTime.now();
		String dateTime = "" + ldt.getYear() + "." + ldt.getMonthValue() + "."
				+ ldt.getDayOfMonth() + " " + ldt.getHour() + ":"
				+ ldt.getMinute() + ":" + ldt.getSecond();
		Charset cs = StandardCharsets.UTF_8;
		try {
			errorLog = Files
					.newBufferedWriter(
							Paths.get("log", dateTime, "error.log"), cs);
		} catch (IOException e) {
			System.err
					.println("Не удалось создать файл для логгирования ошибок");
			e.printStackTrace();
		}
		try {
			warningLog = Files
					.newBufferedWriter(
							Paths.get("log", dateTime, "warning.log"),
							cs);
		} catch (IOException e) {
			System.err
					.println("Не удалось создать файл для логгирования ошибок");
			e.printStackTrace();
		}
		try {
			messageLog = Files
					.newBufferedWriter(
							Paths.get("log", dateTime, "message.log"),
							cs);
		} catch (IOException e) {
			System.err
					.println("Не удалось создать файл для логгирования ошибок");
			e.printStackTrace();
		}
	}

	public enum MsgType {
		MESSAGE, WARNING, ERROR
	}

	public void print(String msg, MsgType type) {
		switch (type) {
		case MESSAGE:
			break;
		case WARNING:
			break;
		case ERROR:
			break;
		}
	}
}
