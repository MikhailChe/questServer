package quest.model.quest1;

import static quest.controller.log.QLog.MsgType.INFO;
import static quest.controller.log.QLog.MsgType.WARNING;

import java.net.DatagramPacket;

import quest.controller.log.QLog;
import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.PacketData;
import quest.model.common.ifaces.InputByteProcessor;

public class SportRings extends MicroUnit implements InputByteProcessor {
	public SportRings() {
		super(null, "Спортивные кольца");
	}

	private int[] weight = { 0, 0 };
	private boolean magneticLock = false;

	void setWeight(int index, int weight) {
		this.weight[index] = weight;
	}

	public int getWeight() {
		return this.weight[0] + this.weight[1];
	}

	void setMagneticLock(boolean value) {
		this.magneticLock = value;
	}

	public boolean isMagneticLocked() {
		return this.magneticLock;
	}

	public void lock(boolean value) {
		DatagramPacket packet = null;
		if (value) {
			packet = relayOpen();
		} else {
			packet = relayClose();
		}
		send(packet);

	}

	public DatagramPacket relayOpen() {
		return datagramForData(3, true, new byte[] { (byte) 1 });
	}

	public DatagramPacket relayClose() {
		return datagramForData(3, true, new byte[] { (byte) 0 });
	}

	@Override
	public void processInput(byte[] data) {
		PacketData pack = new PacketData(data);

		if (pack.data.length > 0) {
			switch (pack.perifiral) {
			case 1:
			case 2:
				if (pack.data.length == 2) {
					setWeight(pack.perifiral - 1, shortFromByteArray(pack.data));
					QLog.inst().print("Устанавливаем новый вес: " + getWeight(), INFO);
				} else {
					QLog.inst().print("Пришли данные неверной длины: " + this.getName() + ", перефирия" + data[0],
							WARNING);
				}
				break;
			case 3:
				if (pack.data.length == 1) {
					setMagneticLock(boolFromByteArray(pack.data));
					QLog.inst().print("Устанавливаем замок: " + isMagneticLocked(), INFO);
				} else {
					QLog.inst().print("Пришли данные неверной длины: " + this.getName() + ", перефирия" + data[0],
							WARNING);
				}
			}
		}
	}
}
