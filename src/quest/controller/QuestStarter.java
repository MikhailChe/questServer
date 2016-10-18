package quest.controller;

import static quest.controller.log.QLog.MsgType.ERROR;
import static quest.controller.log.QLog.MsgType.INFO;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.bind.JAXB;

import quest.controller.log.QLog;
import quest.controller.net.tcp.QuestHttpServer;
import quest.controller.net.udp.McuUdpServer;
import quest.model.common.classes.MicroUnit;
import quest.model.quest1.QuestXML;
import quest.view.MCULists;
import quest.view.Mainframe;

public class QuestStarter {

	public static QuestHttpServer httpServer;
	public static McuUdpServer udpServer;

	public static void main(String... strings) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
		}

		final QLog LOG = QLog.inst();
		QuestXML quest = null;

		try (InputStream stream = QuestStarter.class.getResourceAsStream("quest.xml");) {
			quest = JAXB.unmarshal(stream, QuestXML.class);
			LOG.print("Конфигурация квеста " + quest + " загружена.", INFO);
		} catch (Exception e) {
			LOG.print("Не смог загрузить конфигурацию квеста: " + e.getLocalizedMessage(), ERROR);

			return;
		}

		try {
			QuestHttpServer httpServer = new QuestHttpServer(quest);
			httpServer.start();
		} catch (IOException e1) {
			QLog.inst().print(e1.getLocalizedMessage(), ERROR);
		}

		LOG.print("Теперь запустим UDP сервер", INFO);
		try {
			udpServer = new McuUdpServer(2016);
			for (MicroUnit unit : quest.units) {
				udpServer.addService(unit);
			}
			new Thread(udpServer).start();
		} catch (SocketException e) {
			LOG.print("Не смог запустить сервер контроллеров" + e.getLocalizedMessage(), ERROR);
		}
		Mainframe frame = new Mainframe(quest.toString());
		frame.setContentPane(new MCULists(quest.units));
		frame.showMe();
		updateAllLoop(quest.units);
	}

	static void updateAllLoop(List<MicroUnit> units) {
		while (true) {
			for (MicroUnit unit : units) {
				unit.initialize();
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				QLog.inst().print("Прерваный цикл?" + e.getLocalizedMessage(), ERROR);
			}
		}
	}
}
