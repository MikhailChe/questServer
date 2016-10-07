package quest.controller.net;

import static quest.controller.log.Logger.MsgType.ERROR;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import quest.controller.log.Logger;

public class Addresser {
	public static SocketAddress getSocketAddress(String ip, int port) {
		InetAddress address;
		try {
			address = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			Logger.inst().print("Неверный ip или имя хоста: " + e.getLocalizedMessage(), ERROR);
			return null;
		}
		return new InetSocketAddress(address, port);
	}
}
