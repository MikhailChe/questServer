package quest.controller;

import static quest.controller.log.QLog.MsgType.ERROR;
import static quest.controller.log.QLog.MsgType.INFO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import quest.controller.log.QLog;
import quest.controller.net.Addresser;
import quest.controller.net.tcp.QuestHttpServer;
import quest.controller.net.udp.McuUdpServer;
import quest.model.common.classes.MicroUnit;
import quest.model.quest1.TestingStructure;

public class QuestStarter {

	public static QuestHttpServer httpServer;
	public static McuUdpServer udpServer;

	public static void main(String... strings) {

		final QLog LOG = QLog.inst();
		TestingStructure quest = new TestingStructure();
		System.out.println("Введите IP адрес для контроллера:");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));) {
			InetSocketAddress addr = null;
			while ((addr = Addresser.getSocketAddress(reader.readLine(), 1024)) == null)
				;
			quest.rings.setAddress(addr);
		} catch (IOException e2) {
			LOG.print(e2.getLocalizedMessage(), ERROR);
			return;
		}

		LOG.print("Теперь запустим UDP сервер", INFO);
		try {
			udpServer = new McuUdpServer(2016);
			for (MicroUnit unit : MicroUnit.getMicrounits(quest)) {
				udpServer.addService(unit);
			}
			new Thread(udpServer).start();
		} catch (SocketException e) {
			LOG.print("Не смог запустить сервер контроллеров", ERROR);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		quest.rings.lock(true);

	}

}
