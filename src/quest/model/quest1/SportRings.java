package quest.model.quest1;

import static quest.controller.log.Logger.MsgType.WARNING;

import java.net.DatagramPacket;
import java.net.SocketAddress;

import quest.controller.log.Logger;
import quest.model.common.classes.MicroUnit;
import quest.model.common.ifaces.InputByteProcessor;

public class SportRings extends MicroUnit implements InputByteProcessor {
	public SportRings(SocketAddress addr) {
		super(addr);

	}

	int weight = 0;
	boolean magneticLock = false;

	public int getWeight() {
		return this.weight;
	}

	public boolean isMagneticLocked() {
		return this.magneticLock;
	}

	public void lock(boolean value) {
		if (value) {
			relayOpen();
		} else {
			// relayClose
		}
	}

	public DatagramPacket relayOpen() {
		String response = "\"relay\":true";
		return datagramForData(0, true, response.getBytes());
	}

	@Override
	public void processInput(byte[] data) {
		switch (data[0]) {
		case 0: {
			if (data.length >= 5) {
				int newWeight = data[1] + data[2] << 8 + data[3] << 16 + data[4] << 24;
				this.weight = newWeight;
			}
			break;
		}
		case 1: {
			if (data.length >= 2) {
				if (data[1] == 0) {
					this.magneticLock = false;
				} else {
					this.magneticLock = true;
				}
			}
			break;
		}
		default:
			Logger.inst().print("Пришли данные, о которых мы не знаем", WARNING);
			break;
		}
	}
}
