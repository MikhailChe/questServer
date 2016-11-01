package quest.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import quest.controller.net.Addresser;
import quest.model.common.classes.PacketData;

public class QuestMock {
	public static void main(String[] args) {
		try (DatagramSocket datagramSocket = new DatagramSocket(1024);) {
			byte[] data = new PacketData((byte) 254, true, new byte[] { 0}).getBytes();

			try {
				datagramSocket
						.send(new DatagramPacket(data, data.length, Addresser.getSocketAddress("127.0.0.1", 2016)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
