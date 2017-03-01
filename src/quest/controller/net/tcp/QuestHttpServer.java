package quest.controller.net.tcp;

import static quest.controller.log.QLog.MsgType.INFO;
import static quest.controller.log.QLog.MsgType.WARNING;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import quest.controller.log.QLog;
import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.fields.Property;
import quest.model.quest.QuestXML;

public class QuestHttpServer {

	public static Map<String, List<String>> decodeRawQuery(String raw) {
		if (raw == null || raw.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, List<String>> hashmap = new ConcurrentHashMap<>();

		String[] pieces = raw.split("&");

		for (String pair : pieces) {
			String[] keyval = pair.split("=");

			if (keyval.length == 1) {
				try {
					String key = URLDecoder.decode(keyval[0], StandardCharsets.UTF_8.name());
					hashmap.computeIfAbsent(key, k -> {
						return new ArrayList<>();
					});
				} catch (UnsupportedEncodingException e) {
					QLog.inst().print("Неудачная попытка кодирования-декодирования данных от клиента.", WARNING);
				}
			} else if (keyval.length == 2) {
				String key = keyval[0];
				String value = keyval[1];

				try {
					String keyD = URLDecoder.decode(key, StandardCharsets.UTF_8.name());
					String valD = URLDecoder.decode(value, StandardCharsets.UTF_8.name());

					hashmap.compute(keyD, (k, v) -> {
						if (v == null) {
							v = new ArrayList<>();
						}
						v.add(valD);
						return v;
					});
				} catch (UnsupportedEncodingException e) {
					QLog.inst().print("Неудачная попытка кодирования-декодирования данных от клиента.", WARNING);
				}
			}
		}

		return hashmap;
	}

	private HttpServer httpServer;

	public QuestHttpServer(QuestXML quest) throws UnknownHostException, IOException {

		this.httpServer = HttpServer.create(new InetSocketAddress(80), 0);
		this.httpServer.createContext("/", new ProHandler() {
			@Override
			public void handlePro(HttpExchange t) throws IOException {

				URI uri = t.getRequestURI();

				String path = uri.getPath();
				if (path.isEmpty() || "/".equals(path)) {
					path = "/index.html";
				}

				path = "www" + path;
				QLog.inst().print("Отдаю файл из корневой директории: " + path, INFO);

				try {
					String checkPath = path;
					t.getResponseHeaders().compute("Content-type", (k, v) -> {
						String probe = "";
						try {
							probe = Files.probeContentType(Paths.get(checkPath));
						} catch (IOException e) {
							QLog.inst().print("Не смог получить MIME тип файла.", WARNING);
						}
						if (probe.contains("text")) {
							probe += "; charset=utf-8";
						}
						QLog.inst().print("Тип MIME: " + probe, INFO);
						return Collections.singletonList(probe);
					});

					try (InputStream is = QuestHttpServer.class.getResourceAsStream(path)) {
						byte[] buf = new byte[is.available()];
						is.read(buf);
						t.sendResponseHeaders(HttpURLConnection.HTTP_ACCEPTED, buf.length);
						t.getResponseBody().write(buf);
					}
				} catch (Exception e) {
					QLog.inst().print("404 страница не найдена: " + e.getClass().getSimpleName(), WARNING);
					t.getResponseHeaders().add("Content-type", "text/html; charset=utf-8");
					String response404 = "404 (страница не найдена)";
					byte[] buf = response404.getBytes(StandardCharsets.UTF_8);
					t.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, buf.length);
					t.getResponseBody().write(buf);
				}
				t.close();

			}
		});
		this.httpServer.createContext("/api", new ProHandler() {
			@Override
			public void handlePro(HttpExchange t) throws IOException {
				QLog.inst().print("Отдаю ответ на API запрос: " + t.getHttpContext().getPath() + ", "
						+ t.getRequestURI().toString(), INFO);
				t.getResponseHeaders().add("Content-type", "application/json; charset=utf-8");

				StringBuilder sb = new StringBuilder();

				sb.append('{');
				{
					sb.append("\"units\":[");

					sb.append(quest.units.stream().map(MicroUnit::getName).map(s -> "\"" + s + "\"")
							.collect(Collectors.joining(",")));

					sb.append(']');
				}
				sb.append('}');

				String response = sb.toString();
				byte[] output = response.toString().getBytes(StandardCharsets.UTF_8);

				t.sendResponseHeaders(200, output.length);
				t.getResponseBody().write(output);
				t.close();
			}
		});

		for (MicroUnit unit : quest.units) {
			try {
				final String unitName = new URI(unit.getName().replaceAll(" ", "%20")).getPath();
				System.out.println(unitName);
				this.httpServer.createContext("/api/" + unitName, new ProHandler() {
					@Override
					public void handlePro(HttpExchange t) throws IOException {
						QLog.inst().print(
								"Отдаю список параметров для " + unitName + ": " + t.getHttpContext().getPath(), INFO);

						t.getResponseHeaders().add("Content-type", "application/json; charset=utf-8");

						StringBuilder sb = new StringBuilder();
						sb.append('{');
						{
							sb.append("\"units\":[");

							sb.append(unit.getProperties().stream().map(Property::getName).map(s -> "\"" + s + "\"")
									.collect(Collectors.joining(",")));

							sb.append(']');
						}
						sb.append('}');

						String response = sb.toString();
						byte[] output = response.toString().getBytes(StandardCharsets.UTF_8);

						t.sendResponseHeaders(200, output.length);
						t.getResponseBody().write(output);
						t.close();
					}
				});
				for (Property prop : unit.getProperties()) {
					this.httpServer.createContext("/api/" + unitName + "/" + prop.getName(), new ProHandler() {
						@Override
						public void handlePro(HttpExchange t) throws IOException {
							QLog.inst().print("Отдаю состояние устройства " + unitName + "/" + prop.getName() + ": "
									+ t.getHttpContext().getPath(), INFO);

							t.getResponseHeaders().add("Content-type", "application/json; charset=utf-8");

							StringBuilder sb = new StringBuilder();
							sb.append('{');
							{
								sb.append("\"" + prop.getName() + "\":");

								sb.append("\"" + prop.getValue() + "\"");

							}
							sb.append('}');

							String response = sb.toString();
							byte[] output = response.toString().getBytes(StandardCharsets.UTF_8);

							t.sendResponseHeaders(200, output.length);
							t.getResponseBody().write(output);
							t.close();
						}
					});
				}
			} catch (URISyntaxException e) {
				QLog.inst().print(
						"Пытался преобразовать пробел в %20, но ничего не получилось. " + e.getLocalizedMessage(),
						WARNING);
			}
		}
		QLog.inst().print("Добавлены контексты для веб-сервера", INFO);
		this.httpServer.setExecutor(null);
	}

	public void start() {
		this.httpServer.start();
		QLog.inst().print("Веб-сервер запущен и готов работать", INFO);
	}

}
