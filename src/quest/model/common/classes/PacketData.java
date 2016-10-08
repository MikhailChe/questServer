package quest.model.common.classes;

import java.util.Arrays;

public class PacketData {

	public byte perifiral = 0;
	public boolean write = false;
	public byte[] data;

	public PacketData(byte perifiral, boolean write, byte[] data) {
		this.perifiral = perifiral;
		this.write = write;
		this.data = data;
	}

	public PacketData(byte[] data) {
		perifiral = data[0];
		write = data[1] != 0 ? true : false;
		this.data = Arrays.copyOfRange(data, 4, data.length + 4);
	}

	public byte[] getBytes() {
		byte[] out = new byte[data.length + 4];
		out[0] = perifiral;
		out[1] = write ? (byte) 1 : (byte) 0;
		out[3] = (byte) this.data.length;
		for (int i = 4; i < out.length; i++) {
			out[i] = data[i - 4];
		}
		return out;
	}

}
