package quest.controller;

import static quest.controller.log.QLog.MsgType.ERROR;
import static quest.controller.log.QLog.MsgType.INFO;

import java.io.File;
import java.net.SocketException;

import javax.xml.bind.JAXB;

import quest.controller.log.QLog;
import quest.controller.net.Addresser;
import quest.controller.net.tcp.QuestHttpServer;
import quest.controller.net.udp.McuUdpServer;
import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.fields.Property;
import quest.model.quest1.QuestXML;

public class QuestStarter {

	public static QuestHttpServer httpServer;
	public static McuUdpServer udpServer;

	public static void main(String... strings) {

		QuestXML quest = new QuestXML();
		MicroUnit unit = new MicroUnit();
		unit.setAddress(Addresser.getSocketAddress("192.168.243.7", 1024));
		unit.property.add(new Property((byte) 1, "Имечко1", Boolean.class, true));
		unit.property.add(new Property((byte) 2, "Имечко2", Short.class, true));
		unit.property.get(0).setValue(new byte[] { 0 });
		unit.property.get(1).setValue(new byte[] { (byte) 255, (byte) 128 });
		unit.setName("Hello");
		quest.units.add(unit);
		quest.units.add(unit);
		JAXB.marshal(quest, new File("quest.xml"));

		QuestXML quester = JAXB.unmarshal(new File("quest.xml"), QuestXML.class);

		quester.units.forEach(System.out::println);

	}

	public static void main2(String... strings) {

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
