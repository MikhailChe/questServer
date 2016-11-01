package quest.controller;

import static quest.controller.log.QLog.MsgType.ERROR;
import static quest.controller.log.QLog.MsgType.INFO;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import quest.controller.log.QLog;
import quest.controller.net.tcp.QuestHttpServer;
import quest.controller.net.udp.McuUdpServer;
import quest.model.common.classes.MicroUnit;
import quest.model.quest.QuestXML;
import quest.view.Mainframe;
import quest.view.McuAddressesGUI;

public class QuestStarter {

	public static QuestHttpServer httpServer;
	public static McuUdpServer udpServer;

	public static void main(String... strings) {
		final QLog LOG = QLog.inst();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			LOG.print("Не смог изменить вид интерфейса на системный. " + e.getLocalizedMessage(), INFO);
		}

		QuestXML quest = null;

		try (InputStream stream = new FileInputStream(new File("quest.xml"))) {
			quest = QuestXML.loadXML(stream);
			LOG.print("Конфигурация квеста " + quest + " загружена.", INFO);
		} catch (Exception e) {
			LOG.print("Не смог загрузить конфигурацию квеста: " + e.getLocalizedMessage(), ERROR);
			return;
		}

		try {
			QuestHttpServer httpServer = new QuestHttpServer(quest);
			httpServer.start();
		} catch (IOException e) {
			QLog.inst().print("Не удалось запустить HTTP сервер. " + e.getLocalizedMessage(), ERROR);
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
			try {
				JOptionPane.showMessageDialog(null, "Не удалось запустить сервер контроллеров.\nЗавершение работы.");
			} catch (HeadlessException headless) {
			}
			System.exit(0);
		}
		Mainframe frame = new Mainframe(quest.toString());
		// frame.setContentPane(new MCULists(quest.units));
		frame.setContentPane(new McuAddressesGUI(quest, quest.units, frame));
		frame.showMe();
		// updateAllLoop(quest.units);
	}

	public static void updateAllLoop(List<MicroUnit> units) {
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
