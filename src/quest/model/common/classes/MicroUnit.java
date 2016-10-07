package quest.model.common.classes;

import java.net.DatagramPacket;
import java.net.SocketAddress;

public class MicroUnit {
	private SocketAddress innerAddress;

	public MicroUnit(SocketAddress addr) {
		this.innerAddress = addr;
	}

	public SocketAddress getAddress() {
		return this.innerAddress;
	}

	protected DatagramPacket datagramForData(int perifiral, boolean write, byte[] data) {
		byte[] outputData = new byte[data.length + 4];
		outputData[0] = (byte) perifiral;
		outputData[1] = (byte) (write ? 1 : 0);
		outputData[3] = (byte) data.length;
		for (int i = 4; i < outputData.length; i++) {
			outputData[i] = data[i - 4];
		}

		return new DatagramPacket(outputData, outputData.length, this.innerAddress);
	}
}
