package quest.controller.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
	static volatile Logger	instance;

	BufferedWriter			errorLog;
	BufferedWriter			warningLog;
	BufferedWriter			infoLog;

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

	private Logger() {
		LocalDateTime ldt = LocalDateTime.now();
		String dateTime = "" + ldt.getYear() + "." + ldt.getMonthValue() + "."
				+ ldt.getDayOfMonth() + " " + ldt.getHour() + "-"
				+ ldt.getMinute() + "-" + ldt.getSecond();
		dateTime = ldt
				.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH-mm-ss"));
		Charset cs = StandardCharsets.UTF_8;

		Path parentPath = Paths.get("log", dateTime);
		if (Files.notExists(parentPath, LinkOption.NOFOLLOW_LINKS)) {
			try {
				Files.createDirectories(parentPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			Path errorPath = parentPath.resolve("error.log");

			errorLog = Files
					.newBufferedWriter(
							errorPath,
							StandardOpenOption.CREATE,
							StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.err
					.println("Не удалось создать файл для логгирования ошибок");
			e.printStackTrace();
		}
		try {
			Path warningPath = parentPath.resolve("warning.log");
			warningLog = Files
					.newBufferedWriter(
							warningPath,
							cs,
							StandardOpenOption.CREATE,
							StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.err
					.println(
							"Не удалось создать файл для логгирования предупреждений");
			e.printStackTrace();
		}
		try {
			Path infoPath = parentPath.resolve("info.log");
			infoLog = Files
					.newBufferedWriter(
							infoPath,
							cs,
							StandardOpenOption.CREATE,
							StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.err
					.println("Не удалось создать файл для логгирования ошибок");
			e.printStackTrace();
		}
	}

	public enum MsgType {
		INFO, WARNING, ERROR
	}

	public void print(String msg, MsgType type) {
		String dateTime = "" + System.currentTimeMillis();
		String msgToOutput = String.format("%s:\t%s%n", dateTime, msg);
		switch (type) {
		case INFO:
			System.out.print(msgToOutput);
			try {
				infoLog.write(msgToOutput);
				infoLog.flush();
			} catch (IOException | NullPointerException e) {
				e.printStackTrace();
			}
			break;
		case WARNING:
			System.err.print(msgToOutput);
			try {
				warningLog.write(msgToOutput);
				warningLog.flush();
			} catch (IOException | NullPointerException e) {
				e.printStackTrace();
			}
			break;
		case ERROR:
			System.err.print(msgToOutput);
			try {
				errorLog.write(msgToOutput);
				errorLog.flush();
			} catch (IOException | NullPointerException e) {
				e.printStackTrace();
			}
			break;
		}
	}
}
