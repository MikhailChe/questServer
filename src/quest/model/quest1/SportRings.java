package quest.model.quest1;

import static quest.controller.log.Logger.MsgType.WARNING;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import quest.controller.log.Logger;
import quest.model.ifaces.InputByteProcessor;

public class SportRings implements InputByteProcessor {
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
		DatagramPacket dp;
		try {
			dp = new DatagramPacket(response.getBytes(), response.getBytes().length,
					InetAddress.getByAddress(new byte[] { (byte) 192, (byte) 168, (byte) 243, 2 }), 2016);
			return dp;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void tenzoProcess(byte[] data) {
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

	@Override
	public void processInput(byte[] data) {
		// TODO Auto-generated method stub

	}
}
