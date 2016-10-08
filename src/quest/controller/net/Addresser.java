package quest.controller.net;

import static quest.controller.log.QLog.MsgType.ERROR;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import quest.controller.log.QLog;

public class Addresser {
	public static InetSocketAddress getSocketAddress(String ip, int port) {
		InetAddress address;
		try {
			address = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			QLog.inst().print("Неверный ip или имя хоста: " + e.getLocalizedMessage(), ERROR);
			return null;
		}
		return new InetSocketAddress(address, port);
	}
}
