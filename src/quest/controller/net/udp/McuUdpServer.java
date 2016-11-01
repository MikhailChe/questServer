package quest.controller.net.udp;

import static quest.controller.log.QLog.MsgType.INFO;
import static quest.controller.log.QLog.MsgType.WARNING;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;

import quest.controller.log.QLog;
import quest.controller.log.QLog.MsgType;
import quest.model.common.classes.MicroUnit;
import quest.model.common.ifaces.InputByteProcessor;

public class McuUdpServer implements Runnable, AutoCloseable {

	public final DatagramSocket socket;
	public final Map<InetSocketAddress, Consumer<byte[]>> services = Collections.synchronizedMap(new Hashtable<>());

	public McuUdpServer(int port) throws SocketException {
		this.socket = new DatagramSocket(port);
	}

	public void addService(MicroUnit mcu, InputByteProcessor function) {
		try {
			this.services.put(mcu.getAddress(), function::processInput);
			QLog.inst().print("Добавлен UDP сервис для МК: " + mcu.toString(), INFO);
		} catch (Exception e) {
			QLog.inst().print("Проблемы добавления сервиса МК: " + e.getLocalizedMessage(), WARNING);
		}
	}

	public <T extends MicroUnit & InputByteProcessor> void addService(T mcu) {
		addService((MicroUnit) mcu, (InputByteProcessor) mcu);
	}

	@Override
	public void run() {
		final QLog LOG = QLog.inst();
		byte[] inputBuffer = new byte[65535];
		DatagramPacket inputPacket = new DatagramPacket(inputBuffer, inputBuffer.length);
		while (true) {
			try {
				this.socket.receive(inputPacket);
				LOG.print("Пришел пакет от " + inputPacket.getSocketAddress() + ", размер " + inputPacket.getLength()
						+ " байт", INFO);
				Consumer<byte[]> consumer = this.services.get(inputPacket.getSocketAddress());
				if (consumer != null) {
					LOG.print("Знаю кому передать пакет, всё нормально", INFO);
					consumer.accept(Arrays.copyOf(inputPacket.getData(), inputPacket.getLength()));
				} else {
					LOG.print("Пакет, пришедший от " + inputPacket.getSocketAddress()
							+ " никому не принадлежит. Возможно атака на сервер,"
							+ " а возможно просто неверная конфигурация квеста.", WARNING);
				}

			} catch (IOException e) {
				LOG.print("Не смог поулчить UDP сообщение: " + e.getLocalizedMessage(), MsgType.ERROR);
			}
		}
	}

	@Override
	public void close() throws Exception {
		this.socket.close();
	}
}
