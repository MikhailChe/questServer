package quest.controller.net.tcp;

import static quest.controller.log.Logger.MsgType.INFO;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import quest.controller.QuestStarter;
import quest.controller.log.Logger;
import quest.model.quest1.Quest;

public class QuestHttpServer {

	public QuestHttpServer() throws UnknownHostException, IOException {
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(81), 0);
		httpServer.createContext("/").setHandler(new HttpHandler() {
			@Override
			public void handle(HttpExchange t) throws IOException {
				System.out.println("Called handler");

				String response = "<html>" + "<head><meta charset=\"UTF-8\"/></head>" + "<body>\"tensa\":{\"weight\":"
						+ Quest.inst().rings.getWeight() + ",\"relay\":" + Quest.inst().rings.isMagneticLocked()
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
				Quest.inst().rings.lock(true);
				DatagramPacket dp = Quest.inst().rings.relayOpen();
				QuestStarter.udpServer.socket.send(dp);
				String response = "status:ok";
				t.sendResponseHeaders(200, response.getBytes().length);
				t.getResponseBody().write(response.getBytes());
				t.getResponseBody().flush();
				t.getResponseBody().close();
			}
		});
		Logger.inst().print("Добавлены контексты для веб-сервера", INFO);
		httpServer.setExecutor(null);
		httpServer.start();
		Logger.inst().print("Dеб-сервер запущен и готов работать", INFO);

	}

}
