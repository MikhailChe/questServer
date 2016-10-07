package quest.model;

import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class SportRings {
	public int weight = 0;
	public boolean magneticLock = false;

	public DatagramPacket relayOpen() {
		String response = "\"relay\":true";
		DatagramPacket dp;
		try {
			dp = new DatagramPacket(response.getBytes(), response.getBytes().length,
					Inet4Address.getByAddress(new byte[] { (byte) 192, (byte) 168, (byte) 243, 2 }), 2016);
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
		}
	}
}
