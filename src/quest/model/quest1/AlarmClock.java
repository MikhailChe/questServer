package quest.model.quest1;

import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.PacketData;
import quest.model.common.ifaces.InputByteProcessor;

public class AlarmClock extends MicroUnit implements InputByteProcessor {
	public AlarmClock() {
		super(null, "Будильник");
	}

	boolean hall = false;
	boolean clockArmed = false;
	boolean magneticLock = false;

	void setHall(boolean val) {
		this.hall = val;
	}

	void setClockArmed(boolean val) {
		this.clockArmed = val;
	}

	void setMagneticLock(boolean val) {
		this.magneticLock = val;
	}

	public void lock(boolean val) {
		send(datagramForData(3, true, val));
	}

	@Override
	public void processInput(byte[] data) {
		PacketData p = new PacketData(data);
		if (p.data.length > 0) {
			switch (p.perifiral) {
			default:
			case 1:
				setHall(p.data[0] != 0 ? true : false);
				break;
			case 2:
				setClockArmed(p.data[0] != 0 ? true : false);
				break;
			case 3:
				setMagneticLock(p.data[0] != 0 ? true : false);
				break;
			}
		}
	}
}
