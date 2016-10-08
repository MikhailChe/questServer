package quest.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import quest.controller.net.Addresser;

public class MicroUnitMockUDP {
	public static void main(String[] args) {
		try (DatagramSocket sock = new DatagramSocket(1024);) {
			byte[] buffer = new byte[65535];
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			p.setSocketAddress(Addresser.getSocketAddress("127.0.0.1", 2016));
			try {
				byte[] output = new byte[] { 0, 0, 0, 2, 0, 0 };
				p.setData(output);
				p.setLength(output.length);

				System.out.println("Отправили ответ");

				sock.send(p);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (SocketException e) {
			System.err.println("MOCK: " + e.getLocalizedMessage());
		}
	}

}
