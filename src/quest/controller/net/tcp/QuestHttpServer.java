package quest.controller.net.tcp;

import static quest.controller.log.QLog.MsgType.INFO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import quest.controller.log.QLog;

public class QuestHttpServer {

	public QuestHttpServer() throws UnknownHostException, IOException {
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(81), 0);
		httpServer.createContext("/").setHandler(new HttpHandler() {
			@Override
			public void handle(HttpExchange t) throws IOException {
				System.out.println("Called handler");

				String response = "<html>" + "<head><meta charset=\"UTF-8\"/></head>" + "<body>\"tensa\":{\"weight\":"
						+ /* Quest.inst().rings.getWeight() + */ ",\"relay\":" /*
																				 * +
																				 * Quest
																				 * .
																				 * inst
																				 * (
																				 * )
																				 * .
																				 * rings
																				 * .
																				 * isMagneticLocked
																				 * (
																				 * )
																				 */
						+ "}</body>" + "</html>";
				t.sendResponseHeaders(200, response.getBytes().length);
				t.getResponseBody().write(response.getBytes());
				t.getResponseBody().flush();
				t.getResponseBody().close();
			}
		});
		httpServer.createContext("/api/tenza/relay/set").setHandler(new HttpHandler() {
			@Override
			public void handle(HttpExchange t) throws IOException {
				// Quest.inst().rings.lock(true);
				/*
				 * DatagramPacket dp = Quest.inst().rings.relayOpen();
				 * QuestStarter.udpServer.socket.send(dp); String response =
				 * "status:ok"; t.sendResponseHeaders(200,
				 * response.getBytes().length);
				 * t.getResponseBody().write(response.getBytes());
				 * t.getResponseBody().flush(); t.getResponseBody().close();
				 */
			}
		});
		QLog.inst().print("Добавлены контексты для веб-сервера", INFO);
		httpServer.setExecutor(null);
		httpServer.start();
		QLog.inst().print("Dеб-сервер запущен и готов работать", INFO);

	}

}
