package quest.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;

public class UDPServer implements Runnable, AutoCloseable {

	public final DatagramSocket socket;
	public final Map<InetAddress, Consumer<byte[]>> services = Collections.synchronizedMap(new Hashtable<>());

	public UDPServer(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}

	public void addService(byte[] address, Consumer<byte[]> function) {
		try {
			InetAddress addr = Inet4Address.getByAddress(address);
			services.put(addr, function);
		} catch (Exception e) {

		}
	}

	public void run() {
		byte[] inputBuffer = new byte[65535];
		DatagramPacket inputPacket = new DatagramPacket(inputBuffer, inputBuffer.length);
		while (true) {
			try {
				socket.receive(inputPacket);
				Consumer<byte[]> consumer = services.get(inputPacket.getAddress());
				if (consumer != null) {
					consumer.accept(Arrays.copyOf(inputPacket.getData(), inputPacket.getLength()));
				}
			} catch (IOException e) {
				System.err.println("Не смог поулчить UDP сообщение: " + e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void close() throws Exception {
		socket.close();
	}

}
