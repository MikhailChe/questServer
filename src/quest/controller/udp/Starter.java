package quest.controller.udp;

import java.io.IOException;
import java.net.SocketException;

import quest.model.Quest;

public class Starter {

	public static QuestHttpServer	httpServer;
	public static UDPServer			udpServer;

	public static void main(String... strings) {
		try {
			httpServer = new QuestHttpServer();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		System.out.println("Going to start udp server");
		try {
			udpServer = new UDPServer(2016);
			udpServer.addService(
					new byte[] { (byte) 192, (byte) 168, (byte) 243, 2 },
					Quest.inst().tenzo::tenzoProcess);
			udpServer.addService(
					new byte[] { (byte) 192, (byte) 168, (byte) 243, 3 },
					Quest.inst().infoPaper::infoPaperProcess);
			new Thread(udpServer).start();
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
	}

}
