package quest.controller.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import quest.controller.Starter;
import quest.model.Quest;

public class QuestHttpServer {

	public QuestHttpServer() throws UnknownHostException, IOException {
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(81), 0);
		httpServer.createContext("/").setHandler(new HttpHandler() {
			@Override
			public void handle(HttpExchange t) throws IOException {
				System.out.println("Called handler");

				String response = "<html>"
						+ "<head><meta charset=\"UTF-8\"/></head>"
						+ "<body>\"tensa\":{\"weight\":"
						+ Quest.inst().tenzo.weight + ",\"relay\":"
						+ Quest.inst().tenzo.relay + "}</body>" + "</html>";
				t.sendResponseHeaders(200, response.getBytes().length);
				t.getResponseBody().write(response.getBytes());
				t.getResponseBody().flush();
				t.getResponseBody().close();
			}
		});
		httpServer.createContext("/api/tenza/relay/set")
				.setHandler(new HttpHandler() {
					@Override
					public void handle(HttpExchange t) throws IOException {
						Quest.inst().tenzo.relay = true;
						DatagramPacket dp = Quest.inst().tenzo.relayOpen();
						Starter.udpServer.socket.send(dp);
						String response = "status:ok";
						t.sendResponseHeaders(200, response.getBytes().length);
						t.getResponseBody().write(response.getBytes());
						t.getResponseBody().flush();
						t.getResponseBody().close();
					}
				});
		System.out.println("added context");
		httpServer.setExecutor(null);
		httpServer.start();
		System.out.println("started");
	}

}
