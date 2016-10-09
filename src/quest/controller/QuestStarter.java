package quest.controller;

import static quest.controller.log.QLog.MsgType.ERROR;
import static quest.controller.log.QLog.MsgType.INFO;

import java.io.File;
import java.net.SocketException;

import javax.xml.bind.JAXB;

import quest.controller.log.QLog;
import quest.controller.net.tcp.QuestHttpServer;
import quest.controller.net.udp.McuUdpServer;
import quest.model.common.classes.MicroUnit;
import quest.model.quest1.QuestXML;

public class QuestStarter {

	public static QuestHttpServer httpServer;
	public static McuUdpServer udpServer;

	public static void main(String... strings) {

		final QLog LOG = QLog.inst();
		if (strings.length != 2) {
			LOG.print("Для запуска нужны 2 параметра: IP адрес устройства и порт", ERROR);
			return;
		}

		QuestXML quest = JAXB.unmarshal(new File("quest.xml"), QuestXML.class);

		LOG.print("Теперь запустим UDP сервер", INFO);
		try {
			udpServer = new McuUdpServer(2016);
			for (MicroUnit unit : quest.units) {
				udpServer.addService(unit);
			}
			new Thread(udpServer).start();
		} catch (SocketException e) {
			LOG.print("Не смог запустить сервер контроллеров", ERROR);
		}
		for (MicroUnit unit : quest.units) {
			unit.initialize();
		}
	}
}
