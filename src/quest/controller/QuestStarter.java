package quest.controller;

import static quest.controller.log.Logger.MsgType.ERROR;

import java.io.IOException;
import java.net.SocketException;

import quest.controller.log.Logger;
import quest.controller.net.tcp.QuestHttpServer;
import quest.controller.net.udp.McuUdpServer;
import quest.model.quest1.Quest;

public class QuestStarter {

	public static QuestHttpServer httpServer;
	public static McuUdpServer udpServer;

	public static void main(String... strings) {
		final Logger LOG = Logger.inst();

		try {
			httpServer = new QuestHttpServer();
		} catch (IOException e1) {
			LOG.print("Не смог запустить веб-сервер. " + e1.getLocalizedMessage(), ERROR);
			return;
		}
		System.out.println("Going to start udp server");
		try {
			udpServer = new McuUdpServer(2016);
			udpServer.addService(Quest.inst().rings, Quest.inst().rings::processInput);
			udpServer.addService(Quest.inst().infoPaper, Quest.inst().infoPaper::infoPaperProcess);
			new Thread(udpServer).start();
		} catch (SocketException e) {
			LOG.print("Не смог запустить сервер контроллеров", ERROR);
			return;
		}
	}

}
