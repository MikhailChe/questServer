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

	public PacketData(byte perifiral, boolean write, boolean data) {
		this.perifiral = perifiral;
		this.write = write;
		this.data = new byte[] { data ? (byte) 1 : 0 };
	}

	public PacketData(byte perifiral, boolean write, byte data) {
		this.perifiral = perifiral;
		this.write = write;
		this.data = new byte[] { data };
	}

	public PacketData(byte perifiral, boolean write, short data) {
		this.perifiral = perifiral;
		this.write = write;
		this.data = new byte[] { (byte) (data & 0xff), (byte) ((data >> 8) & 0xff) };
	}

	public PacketData(byte perifiral, boolean write, int data) {
		this.perifiral = perifiral;
		this.write = write;
		this.data = new byte[] { (byte) (data & 0xff), (byte) ((data >> 8) & 0xff), (byte) ((data >> 16) & 0xff),
				(byte) ((data >> 24) & 0xff) };
	}

	public PacketData(byte[] data) {
		this.perifiral = data[0];
		this.write = data[1] != 0 ? true : false;
		short dataLength = data[3];
		this.data = Arrays.copyOfRange(data, 4, 4 + dataLength);
	}

	public byte[] getBytes() {
		byte[] out = new byte[this.data.length + 4];
		out[0] = this.perifiral;
		out[1] = this.write ? (byte) 1 : (byte) 0;
		out[3] = (byte) this.data.length;
		for (int i = 4; i < out.length; i++) {
			out[i] = this.data[i - 4];
		}
		return out;
	}

	@Override
	public String toString() {
		return "периф-аддр: " + this.perifiral + ", " + (this.write ? "запись" : "чтение") + " "
				+ Arrays.toString(this.data);
	}
}
