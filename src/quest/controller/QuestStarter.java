package quest.controller;

import static quest.controller.log.QLog.MsgType.ERROR;
import static quest.controller.log.QLog.MsgType.INFO;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;

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
		if (strings.length != 2) {
			LOG.print("Для запуска нужны 2 параметра: IP адрес устройства и порт", ERROR);
			return;
		}
		int port = 0;
		try {
			port = Integer.parseInt(strings[1]);
		} catch (Exception e) {
			LOG.print("Порт какой-то неправильный. Не удалось преобразовать его в число", ERROR);
			return;
		}

		InetSocketAddress addr = null;
		if ((addr = Addresser.getSocketAddress(strings[0], port)) == null) {
			LOG.print("Это не адрес. Это какая-то ерунда.", ERROR);
			return;
		}

		TestingStructure quest = new TestingStructure();
		quest.rings.setAddress(addr);

		LOG.print("Теперь запустим UDP сервер", INFO);
		try {
			udpServer = new McuUdpServer(2016);
			for (MicroUnit unit : MicroUnit.getMicrounits(quest)) {
				udpServer.addService(unit);
				unit.initialize();
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
		try (Scanner scanner = new Scanner(System.in);) {
			for (int i = 0; i < 10; i++) {
				System.out.println("Введите булевое значение для замка:");
				quest.rings.lock(getBoolean(scanner));
			}
		}
		System.exit(0);
	}

	public static boolean getBoolean(Scanner s) {
		while (!s.hasNextBoolean()) {
			System.err.print("Нужно булевое значение: ");
			s.nextLine();
		}
		return s.nextBoolean();
	}

}
