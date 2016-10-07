package quest.controller;

import static quest.controller.log.Logger.MsgType.ERROR;
import static quest.controller.log.Logger.MsgType.INFO;
import static quest.controller.log.Logger.MsgType.WARNING;

import java.io.IOException;
import java.net.SocketException;

import quest.controller.log.Logger;
import quest.controller.udp.QuestHttpServer;
import quest.controller.udp.UDPServer;
import quest.model.Quest;

public class Starter {

	public static QuestHttpServer	httpServer;
	public static UDPServer			udpServer;

	public static void main(String... strings) {
		Logger log = Logger.inst();

		log.print("Hello, this is info1", INFO);
		log.print("Hello, this is info2", INFO);
		log.print("Hello, this is warning1", WARNING);
		log.print("Hello, this is warning2", WARNING);
		log.print("Hello, this is error1", ERROR);
		log.print("Hello, this is error2", ERROR);

	}

	public static void main2(String... strings) {
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
