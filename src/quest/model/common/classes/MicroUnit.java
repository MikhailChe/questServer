package quest.model.common.classes;

import static quest.controller.log.QLog.MsgType.ERROR;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import quest.controller.QuestStarter;
import quest.controller.log.QLog;
import quest.model.common.ifaces.InputByteProcessor;

public abstract class MicroUnit implements InputByteProcessor {
	private String name;
	private InetSocketAddress innerAddress;

	public MicroUnit() {
		this(null, "Микроконторллер");
	}

	public MicroUnit(InetSocketAddress addr, String name) {
		this.innerAddress = addr;
		this.name = name;
	}

	public void setAddress(InetSocketAddress addr) {
		this.innerAddress = addr;
	}

	public InetSocketAddress getAddress() {
		return this.innerAddress;
	}

	public String getName() {
		return this.name;
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

	protected static void send(DatagramPacket p) {
		if (QuestStarter.udpServer != null) {
			if (QuestStarter.udpServer.socket != null) {
				try {
					QuestStarter.udpServer.socket.send(p);
				} catch (IOException e) {
					QLog.inst().print("Не получилось отправить UDP пакет:" + e.getLocalizedMessage(), ERROR);
				}
			}
		}
	}

	public static List<MicroUnit> getMicrounits(Object o) {
		Field[] fields = o.getClass().getFields();
		List<MicroUnit> list = new ArrayList<>();

		Arrays.asList(fields).stream().filter(f -> MicroUnit.class.isAssignableFrom(f.getType()))
				.filter(f -> InputByteProcessor.class.isAssignableFrom(f.getType())).map(f -> {
					try {
						return f.get(o);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					return null;
				}).forEach(a -> {
					if (a instanceof MicroUnit) {
						list.add((MicroUnit) a);
					}
				});

		return list;
	}

	public static short shortFromByteArray(byte[] arr) {
		int a = 0;
		for (int i = 0; i < arr.length; i++) {
			a += Byte.toUnsignedInt(arr[i]) << (i * 8);
		}
		return (short) a;
	}

	public String toString() {
		return this.name + " " + this.innerAddress;
	}

	@Override
	public abstract void processInput(byte[] data);
}
